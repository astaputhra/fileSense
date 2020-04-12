package com.iMatch;

import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.EtlErrors;
import com.iMatch.etl.IEtlAuth;
import com.iMatch.etl.datauploader.ETLServiceProvider;
import com.iMatch.etl.datauploader.internal.hexFileSense.HexFileSense;
import com.iMatch.etl.datauploader.internal.hexFileSense.SignatureOfEtlFlow;
import com.iMatch.etl.exceptions.UploadError;
import com.iMatch.etl.internal.UploadStatus;
import com.iMatch.etl.models.PreETLErrorDO;
import com.iMatch.etl.orm.IUploadJobMaster;
import com.iMatch.etl.orm.UploadErrors;
import com.iMatch.etl.orm.UploadJobMaster;
import com.iMatch.etl.EtlException;
import com.iMatch.etl.datauploader.EtlJobStats;
import com.iMatch.etl.datauploader.internal.MappingConfig;
import com.iMatch.etl.enums.UploadErrorType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EtlManager {

    private static final Logger logger = LoggerFactory.getLogger(EtlManager.class);

    @Autowired
    IUploadJobMaster iUploadJobMaster;

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager _em;

    private MappingConfig mappingConfig;
    private ETLServiceProvider etlService;
    private HexFileSense hexFileSense;

    private String etlAuthEntity;

    private String etlDebugFile = null;

    public static final String RETRY_ETL = "RETRY_ETL";

    public final Pattern retryCountPattern = Pattern.compile(RETRY_ETL+"~(\\d\\d?)~");

    public void setEtlDebugFile(String etlDebugFile) {
        this.etlDebugFile = etlDebugFile;
    }

    public void setEtlAuthEntity(String etlAuthEntity) {
        this.etlAuthEntity = etlAuthEntity;
    }

    public void runEtlFlow(UploadJobMaster jobMasterEntry, String company, String division) throws EtlException, EtlErrors {
        Map<String, String> params = new HashMap<String, String>();
        params.put("UPD_COMPANY", company);
        params.put("UPD_DIVISION", division);
        params.put("ORIGINAL_FILENAME", jobMasterEntry.getFilename());
//        params.put("HEX_FILE_SENSE_FOLDER", etlDirectoryMonitorDirectory+"/"+company+"/"+division);
        BigDecimal updErrors;
        try {
            logger.debug("Executing etl flow for upload id {}", jobMasterEntry.getUploadId());
            // FIXME restore this to - etlService.uploadData(jobMasterEntry);
            EtlJobStats etlJob = etlService.uploadData(jobMasterEntry.getUploadId(), jobMasterEntry.getFilename(), jobMasterEntry.getEtlFlowName(), jobMasterEntry.getUploadTempTable(), params);

            jobMasterEntry.setNumberOfErrors(etlJob.getNumberOfErrors());
            jobMasterEntry.setNumberOfLinesInput(etlJob.getNumberOfLinesInput());
            jobMasterEntry.setNumberOfLinesOutput(etlJob.getNumberOfLinesOutput());
            jobMasterEntry.setNumberOfRejected(etlJob.getNumberOfRejected());
            jobMasterEntry.setUploadTempTable(jobMasterEntry.getUploadTempTable() == null
                    ? etlJob.getUploadTempTable()
                    : jobMasterEntry.getUploadTempTable());
            if(jobMasterEntry.getNumberOfErrors().compareTo(BigDecimal.ZERO) == 0){
                jobMasterEntry.setStatus(UploadStatus.PROCESS_COMPLETED.toString());
            }else {
                jobMasterEntry.setStatus(UploadStatus.BUSINESS_VALIDATION_FAILED.toString());
//                _em.createNativeQuery("delete from "+jobMasterEntry.getUploadTempTable()).executeUpdate();
//                _em.flush();
            }
            iUploadJobMaster.save(jobMasterEntry);
            iUploadJobMaster.flush();
        } catch (Exception e) {
            jobMasterEntry.setStatus(UploadStatus.PRE_ETL_ERROR.toString());
            iUploadJobMaster.save(jobMasterEntry);
            iUploadJobMaster.flush();
            _em.createNativeQuery("delete from "+jobMasterEntry.getUploadTempTable()).executeUpdate();
            _em.flush();
            populateImEventLog(jobMasterEntry);
            e.printStackTrace();
            logger.error("Error during kettle flow - {}", e.getMessage());
            throw new EtlException(createPreETLDOFromError(jobMasterEntry, e.getMessage()));
        }
        logger.info("Completed Kettle Process for {} & {}",jobMasterEntry.getEtlFlowName(),jobMasterEntry.getFilename());

        populateImEventLog(jobMasterEntry);

        return;
    }

    public void populateImEventLog(UploadJobMaster jobMasterEntry) {
        String query = "INSERT INTO IM_EVENT_LOG(EVENT_MESSAGE_ID,MESSAGE,ACTUAL_PROCESS_MESSAGE,SOURCE_ID,SOURCE_PAIR_ID,IS_ACTIVE,REC_VERSION," +
                "REC_CREATED_BY,REC_CREATED_ON,UPD_IDENTIFIER) SELECT EM.ID,\""+jobMasterEntry.getStatus()+"\" ,\""+jobMasterEntry.getName()+"-"+jobMasterEntry.getStatus()+"\",EM.SOURCE_ID," +
                "EM.SOURCE_PAIR_ID,'Y',1,'System',NOW(),\""+jobMasterEntry.getUploadId()+"\" FROM IM_EVENT_MESSAGE EM INNER      " +
                "JOIN IM_EVENT E ON E.ID = EM.EVENT_ID AND E.EVENT_CODE = 'E0001' INNER " +
                "JOIN IM_MESSAGE M ON M.ID = EM.MESSAGE_ID AND M.MESSAGE_CODE = 'M0002';";
        logger.debug("TaskManager:INSERTING INTO LOGS ----> "+query);
        _em.createNativeQuery(query).executeUpdate();
        _em.flush();
    }

    private PreETLErrorDO createPreETLDOFromError(UploadJobMaster jobMasterEntry, String errorMsg) {
        PreETLErrorDO errorDO = new PreETLErrorDO();
        errorDO.setUploadId(jobMasterEntry.getUploadId());
        errorDO.setUserId(jobMasterEntry.getUserId());
        errorDO.setDownloadedFile(jobMasterEntry.getArchiveFilename());
        errorDO.setDataEmailIdIn(jobMasterEntry.getExtEmailId());
        errorDO.setGenericType(jobMasterEntry.getUploadGenericType());
        errorDO.setDisplayName(jobMasterEntry.getName());
        errorDO.setChannel(jobMasterEntry.getChannel());
        errorDO.setArchivePath(jobMasterEntry.getArchiveFilename());
        errorDO.setZipFilename(jobMasterEntry.getZipFilename());
        errorDO.setDataEmailIdOut(jobMasterEntry.getDataEmailIdOut());
        errorDO.setOriginalFileName(jobMasterEntry.getFilename());

        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("sysErrMsg", errorMsg);
        attributes.put("errFile", FilenameUtils.getName(jobMasterEntry.getFilename()));
        errorDO.setError(new UploadError(attributes, UploadErrorType.SYSTEM_ERROR));
        return errorDO;
    }


    private int getNoOfRetriesFromUploadError(String errorMsg) {
        int maxRedeliveries = 10;
        try {
            final Matcher matcher = retryCountPattern.matcher(errorMsg);
            if (matcher.find())
                return Integer.parseInt(matcher.group(1));
            return maxRedeliveries;
        } catch (Exception e) {
            logger.error("Error finding retry count from error message {}, {} ", errorMsg, e.getMessage());
            return maxRedeliveries;
        }
    }



    public List<SignatureOfEtlFlow> senseFileType(String file, List<String> passwords){
        String expectedFlowname = getDebugFlowName();
        return hexFileSense.senseFileType(file, expectedFlowname, passwords);
    }

    private String getDebugFlowName(){
        Properties prop = new Properties();
        if(etlDebugFile == null)return null;
        try {
            prop.load(new FileInputStream(etlDebugFile));
            return prop.getProperty("flowToDebug");

        } catch (IOException ex) {
            return null;
        }
    }

    public List<String> getPasswords(String company){
        String queryString = "SELECT fileSenseAuth.password FROM FileSenseAuth fileSenseAuth WHERE fileSenseAuth.password is not null AND fileSenseAuth.company is null";
        Query query = _em.createQuery(queryString);
        List<String> companyAgnosticPasswords = query.getResultList();

        if(company == null)return companyAgnosticPasswords;
        queryString = "SELECT fileSenseAuth.password FROM FileSenseAuth fileSenseAuth WHERE fileSenseAuth.password is not null AND fileSenseAuth.company = :company";
        query = _em.createQuery(queryString);
        query.setParameter("company", company);
        List<String> companySpecificPasswords = query.getResultList();
        companySpecificPasswords.addAll(companyAgnosticPasswords);
        return companySpecificPasswords;
    }

    private List<IEtlAuth> getCompanySpecificFlow(String etlFlowName, String company, String division, String userRole){
        String qStr = "select o from " + etlAuthEntity + " o where o.flowName = ?1 AND o.company = ?2 AND o.division = ?3 and o.userRole = coalesce(cast(?4 as string), o.userRole) and o.isActive='Y'";
        Query query = _em.createQuery(qStr);
        query.setParameter(1, etlFlowName);
        query.setParameter(2, company);
        query.setParameter(3, division);
        query.setParameter(4, userRole);

        List<IEtlAuth> etlAuth =  query.getResultList();
        return etlAuth;

    }

    private List<IEtlAuth> getFlowForAnyCompany(String etlFlowName){
        String qStr = null;
        Query query  = null;
        qStr = "select o from " + etlAuthEntity + " o where o.flowName = ?1 and o.isActive='Y'";
        query = _em.createQuery(qStr);
        query.setParameter(1, etlFlowName);
        List<IEtlAuth> etlAuth =  query.getResultList();
        return etlAuth;

    }

    private List<IEtlAuth> getCompanyAgnosticFlow(String etlFlowName, String userRole){
        String qStr = "select o from " + etlAuthEntity + " o where o.flowName = ?1 AND o.isActive='Y'";
        Query query = _em.createQuery(qStr);
        query.setParameter(1, etlFlowName);
//        query.setParameter(2, userRole);

        List<IEtlAuth> etlAuth =  query.getResultList();
        return etlAuth;

    }


    private EtlDefinition populateEtlDefn(IEtlAuth etlAuth) {
        EtlDefinition etlDefn = new EtlDefinition();
        etlDefn.setEtlFlow(etlAuth.getFlowName());
        etlDefn.setDisplayName(etlService.getDisplayNameForFlow(etlAuth.getFlowName()));
        etlDefn.setGenericType(etlService.getEtlTypeForFlow(etlAuth.getFlowName()));
        etlDefn.setApproveUserId(etlAuth.getApproveUserId());
        etlDefn.setCompany(etlAuth.getCompany());
        etlDefn.setDataEmailIdIn(etlAuth.getDataEmailIdIn());
        etlDefn.setDataEmailIdOut(etlAuth.getDataEmailIdOut());
        etlDefn.setDivision(etlAuth.getDivision());
        etlDefn.setIsDivisionSpecific(etlAuth.getIsDivisionSpecific());
        etlDefn.setIsCompanySpecific(etlAuth.getIsCompanySpecific());
        etlDefn.setPreProcessMethod(etlAuth.getPreProcessMethod());
        etlDefn.setPreProcessInput(etlAuth.getPreProcessInput());
        etlDefn.setIsDuplicateAllowed(etlAuth.getIsDuplicateAllowed());
        etlDefn.setUserRole(etlAuth.getUserRole());
        return  etlDefn;
    }


    public EtlDefinition getEtlDefinitionForFlow(String etlFlowName, String company, String division) {
        String userRole =  null;
        if(company == null){
            List<IEtlAuth> companyAgnosticFlow = getCompanyAgnosticFlow(etlFlowName, userRole);
            if(!companyAgnosticFlow.isEmpty()){
                return populateEtlDefn(companyAgnosticFlow.get(0));
            }
            List<IEtlAuth> companySpecificFlow = getFlowForAnyCompany(etlFlowName);
            if(companySpecificFlow.isEmpty()){
                logger.error("Unable to find a flow with name '{}' in table {}", etlFlowName, etlAuthEntity);
                return null;
            }
            return populateEtlDefn(companySpecificFlow.get(0));
        }
        List<IEtlAuth> companySpecificFlow = getCompanySpecificFlow(etlFlowName, company, division, userRole);
        if(!companySpecificFlow.isEmpty()){
            return populateEtlDefn(companySpecificFlow.get(0));
        }
        List<IEtlAuth> companyAgnosticFlow = getCompanyAgnosticFlow(etlFlowName, userRole);
        if(companyAgnosticFlow.isEmpty()){
            logger.error("Unable to find a flow with name '{}' in table {} for company {} and division {}", etlFlowName, etlAuthEntity, company, division);
            return null;
        }
        return populateEtlDefn(companyAgnosticFlow.get(0));
    }

    public MappingConfig getMappingConfig() {
        return mappingConfig;
    }

    public void setMappingConfig(MappingConfig mappingConfig) {
        this.mappingConfig = mappingConfig;
    }

    public ETLServiceProvider getEtlService() {
        return etlService;
    }

    public void setEtlService(ETLServiceProvider etlService) {
        this.etlService = etlService;
    }

    public HexFileSense getHexFileSense() {
        return hexFileSense;
    }

    public void setHexFileSense(HexFileSense hexFileSense) {
        this.hexFileSense = hexFileSense;
    }

    public String getEtlAuthEntity() {
        return etlAuthEntity;
    }

    public String getEtlDebugFile() {
        return etlDebugFile;
    }

    public String removeRetryPrefix(String updErrorMessage) {
        String errorMessage = updErrorMessage;
        try {
            Matcher matcher = retryCountPattern.matcher(updErrorMessage);
            if (matcher.find())
                errorMessage = matcher.replaceFirst("");
            else
                errorMessage = updErrorMessage.replace(RETRY_ETL, "");
        } catch (Exception e) {
            logger.error("Error removing retry prefix ",e);
        }
        return errorMessage;
    }


    /*private BigDecimal findErrorsForUpload(String uploadId) {
        BigInteger count =BigInteger.ZERO;

        List imErrorLogCountList = _em.createNativeQuery("SELECT COUNT(*) FROM IM_EVENT_LOG WHERE UPD_IDENTIFIER = '" + uploadId + "'").getResultList();
        List uploadErrorCountList = _em.createNativeQuery("SELECT COUNT(*) FROM UPLOAD_ERRORS WHERE UPD_IDENTIFIER = '"+uploadId+"'").getResultList();

        if(CollectionUtils.isNotEmpty(imErrorLogCountList) && imErrorLogCountList.size() ==1) count = count.add((BigInteger)imErrorLogCountList.get(0));
        if(CollectionUtils.isNotEmpty(uploadErrorCountList) && uploadErrorCountList.size() ==1) count = count.add((BigInteger) uploadErrorCountList.get(0));

        return BigDecimal.valueOf(count.intValue());
    }*/

}
