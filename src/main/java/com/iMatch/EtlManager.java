package com.iMatch;

import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.EtlErrors;
import com.iMatch.etl.IEtlAuth;
import com.iMatch.etl.datauploader.ETLServiceProvider;
import com.iMatch.etl.datauploader.internal.hexFileSense.HexFileSense;
import com.iMatch.etl.datauploader.internal.hexFileSense.SignatureOfEtlFlow;
import com.iMatch.etl.exceptions.UploadError;
import com.iMatch.etl.models.EtlStatusNotificationDO;
import com.iMatch.etl.models.PreETLErrorDO;
import com.iMatch.etl.orm.UploadErrors;
import com.iMatch.etl.orm.UploadJobMaster;
import com.iMatch.etl.EtlException;
import com.iMatch.etl.datauploader.EtlJobStats;
import com.iMatch.etl.datauploader.internal.MappingConfig;
import com.iMatch.etl.enums.UploadErrorType;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EtlManager {

    private static final Logger logger = LoggerFactory.getLogger(EtlManager.class);

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

        try {
            Context initContext = new InitialContext();
            DataSource ds = (DataSource) initContext.lookup("java:comp/env/jdbc/HexGenJNDI");
            System.out.println("********"+ds.toString());
        } catch (NamingException e) {
//            e.printStackTrace();


        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("UPD_COMPANY", company);
        params.put("UPD_DIVISION", division);
        params.put("ORIGINAL_FILENAME", jobMasterEntry.getOriginalFilename());
//        params.put("HEX_FILE_SENSE_FOLDER", etlDirectoryMonitorDirectory+"/"+company+"/"+division);
        List<UploadErrors> updErrors;
        try {
            logger.debug("Executing etl flow for upload id {}", jobMasterEntry.getUploadId());
            // FIXME restore this to - etlService.uploadData(jobMasterEntry);
            EtlJobStats etlJob = etlService.uploadData(jobMasterEntry.getUploadId(), jobMasterEntry.getFilename(), jobMasterEntry.getEtlFlowName(), jobMasterEntry.getUploadTempTable(), params);

            updErrors = findErrorsForUpload(jobMasterEntry.getUploadId());
            jobMasterEntry.setNumberOfErrors((new BigDecimal(updErrors.size())));
            jobMasterEntry.setNumberOfLinesInput(etlJob.getNumberOfLinesInput());
            jobMasterEntry.setNumberOfLinesOutput(etlJob.getNumberOfLinesOutput());
            jobMasterEntry.setNumberOfRejected(etlJob.getNumberOfRejected());
            jobMasterEntry.setUploadDate(new LocalDate());
            _em.persist(jobMasterEntry);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error during kettle flow - {}", e.getMessage());
            throw new EtlException(createPreETLDOFromError(jobMasterEntry, e.getMessage()));
        }

        if (!jobMasterEntry.getNumberOfErrors().equals(BigDecimal.ZERO)) {
            logger.debug("{} Errors found during ETL, aborting upload",	jobMasterEntry.getNumberOfErrors());

            Map<String, Map<String, String>> errorList = new HashMap<String, Map<String, String>>();
            boolean retryFailed = false;
            for(UploadErrors error : updErrors) {
                String row = "null";
                if(error.getUpdRecordNumber() != null) {
                    row = error.getUpdRecordNumber().toString();
                }
                logger.trace("Setting error for row {}, column {} with message {}", new String[]{ row, error.getUpdColumnName(), error.getUpdErrorMessage() });
                Map<String, String> errorMsg = new HashMap<String, String>();
                if(errorList.containsKey(row)) {
                    errorMsg = errorList.get(row);
                }
                if (error.getUpdErrorMessage() != null && error.getUpdErrorMessage().startsWith(RETRY_ETL)) {
                    int maxRetries = getNoOfRetriesFromUploadError(error.getUpdErrorMessage());
//                    if (event.getRetried() < maxRetries)
//                        throw new HexRetryException(HexGenMessages.getSystemErrorMessage(SystemErrorMessages.RETRY_EXCEPTION), maxRetries);
//                    else {
//                        errorMsg.put(error.getUpdColumnName(), removeRetryPrefix(error.getUpdErrorMessage()));
//                        retryFailed = true;
//                    }
                } else {
                    errorMsg.put(error.getUpdColumnName(), error.getUpdErrorMessage());
                }
                errorList.put(row, errorMsg);
            }

            logger.trace("Setting status notification for FAILED upload");
            EtlStatusNotificationDO etlStatusNotificationDO = new EtlStatusNotificationDO(jobMasterEntry.getUserId(), "FAILED", FilenameUtils.getName(jobMasterEntry.getOriginalFilename()), jobMasterEntry.getArchiveFilename(),
                    jobMasterEntry.getUploadGenericType(), (new LocalDate()), jobMasterEntry.getNumberOfLinesInput(), jobMasterEntry.getNumberOfErrors(),jobMasterEntry.getNumberOfLinesOutput(),
                    jobMasterEntry.getExtEmailId(), errorList, jobMasterEntry.getChannel(), jobMasterEntry.getZipFilename(), jobMasterEntry.getUploadId(), jobMasterEntry.getJobId());
            etlStatusNotificationDO.setCompany(jobMasterEntry.getCompany());
            etlStatusNotificationDO.setDivision(jobMasterEntry.getDivision());
            etlStatusNotificationDO.setEtlFlowName(jobMasterEntry.getEtlFlowName());
            etlStatusNotificationDO.setUploadedBy(jobMasterEntry.getUserId());
            etlStatusNotificationDO.setChecksum(jobMasterEntry.getChecksum());
            etlStatusNotificationDO.setFilePath(jobMasterEntry.getFilename());
            if(jobMasterEntry.getExtEmailId() != null) {
                String dataEmailIdOut = getEtlDefinitionForFlow(jobMasterEntry.getEtlFlowName(), jobMasterEntry.getCompany(), jobMasterEntry.getDivision()).getDataEmailIdOut();
                etlStatusNotificationDO.setDataEmailIdOut(dataEmailIdOut);
            }
            throw new EtlErrors(etlStatusNotificationDO, retryFailed);
        }

        logger.info("No errors occurred during ETL");
        return;
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
        errorDO.setOriginalFileName(jobMasterEntry.getOriginalFilename());
        errorDO.setParentJobId(jobMasterEntry.getJobId());

        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("sysErrMsg", errorMsg);
        attributes.put("errFile", FilenameUtils.getName(jobMasterEntry.getOriginalFilename()));
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
            logger.error("Unable to find a flow with name '{}' in table {} for company {} and division {}", new String[]{etlFlowName, etlAuthEntity, company, division});
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


    private List<UploadErrors> findErrorsForUpload(String uploadId) {
        String queryString = "SELECT uploadErrors FROM UploadErrors uploadErrors WHERE uploadErrors.uploadId = :uploadId";

        logger.trace("Executing query : {}", queryString);
        Query query = _em.createQuery(queryString);
        query.setParameter("uploadId", uploadId);
        logger.trace("Where :uploadId = {}", uploadId);
        return query.getResultList();
    }

}
