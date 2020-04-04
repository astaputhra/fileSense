package com.iMatch.etl.datauploader.internal.kettle;

import com.iMatch.etl.datauploader.internal.hexFileSense.*;
import com.iMatch.etl.datauploader.ETLServiceProvider;
import com.iMatch.etl.datauploader.internal.MappingConfig;
import com.iMatch.etl.datauploader.EtlJobStats;
import com.iMatch.etl.datauploader.internal.SupportedUploadFileTypes;
import org.apache.commons.io.FilenameUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.*;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.parameters.DuplicateParamException;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.filerep.KettleFileRepositoryMeta;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepErrorMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.pentaho.di.trans.steps.databaselookup.DatabaseLookupMeta;
import org.pentaho.di.trans.steps.excelinput.ExcelInputField;
import org.pentaho.di.trans.steps.excelinput.ExcelInputMeta;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.di.trans.steps.tableoutput.TableOutputMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.support.ServletContextResource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.Semaphore;

public class KettleGlue implements ETLServiceProvider, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(KettleGlue.class);

    private MappingConfig mappingConfig;

    @Autowired
    private HexFileSense hexFileSense;

    private String jpavendoradaptor;
    @Value("#{appProp[repoDbVendor]}")
    private String repoDbVendor;
    @Value("#{appProp[repoHostname]}")
    private String repoHostname;
    @Value("#{appProp[repoDbName]}")
    private String repoDbName;
    @Value("#{appProp[repoDbPort]}")
    private String repoDbPort;
    @Value("#{appProp[repoDbUsername]}")
    private String repoDbUsername;
    @Value("#{appProp[repoDbPassword]}")
    private String repoDbPassword;
    @Value("#{appProp[repoUsername]}")
    private String repoUsername;
    @Value("#{appProp[repoPassword]}")
    private String repoPassword;
    @Value("#{appProp[repositoryType]}")
    private String repositoryType;
    @Value("classpath:#{appProp[repoFSName]}")
    private Resource repoFSName;

    private String disableKettleConsoleLogging = "Y";
    private HexKettleLoggingPlugin errorlistener;

//    private Map<String, TransMeta> transMetaCache = new ConcurrentHashMap<>();

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager _em;

    @Value("#{appProp['spring.datasource.url']}")
    String url;

    @Value("#{appProp['spring.datasource.driverClassName']}")
    String driverClassName;

    @Value("#{appProp['spring.datasource.username']}")
    String dbusername;

    @Value("#{appProp['spring.datasource.password']}")
    String password;

    private KettleDatabaseRepository databaseRepository;
    private KettleFileRepository kettleFileRepository;
    private Map<String, SignatureOfEtlFlow> flowToTypeMapping = new HashMap<String, SignatureOfEtlFlow>();
    private Map<String, String> flowToDispNameMaping = new HashMap<String, String>();
    private Map<SupportedUploadFileTypes, List<SignatureOfEtlFlow>> filetypeToSigMaps = new HashMap<SupportedUploadFileTypes, List<SignatureOfEtlFlow>>();
    private Map<String, Map<String, SignatureOfEtlFlow>> uploadTypeToDisplayNameMaps = new HashMap<String, Map<String, SignatureOfEtlFlow>>();
    private StepErrorMeta commonStepErrorMeta =  null;
    private Semaphore etlProcessingPermits;

    public void setMappingConfig(MappingConfig mappingConfig) {
        this.mappingConfig = mappingConfig;
    }

    public String getDbusername() {
        return dbusername;
    }

    public void setDbusername(String dbusername) {
        this.dbusername = dbusername;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setJpavendoradaptor(String jpavendoradaptor){
        this.jpavendoradaptor = jpavendoradaptor;
    }
    private void initFileRepository() throws KettleException {
        logger.info("Initing Kettle with FS repository - {}", repoFSName);

        if (kettleFileRepository == null) {
            String kettleRepoPath;
            kettleRepoPath = "src\\main\\java\\com\\iMatch\\etl\\KettleRepo";
//                logger.debug("Path ",repoFSName.getFile().getPath());
            logger.info("Kettle Repository is - {}", kettleRepoPath);
            kettleFileRepository = new KettleFileRepository();
            KettleFileRepositoryMeta kettleFileRepositoryMeta =
                    new KettleFileRepositoryMeta("KettleFileRepository", "KettleRepoOnFileSystem", "KettleRepoOnFileSystem", kettleRepoPath);
            kettleFileRepository.setRepositoryMeta(kettleFileRepositoryMeta);
            kettleFileRepository.init(kettleFileRepositoryMeta);
        }
    }

    private void initDatabaseRepository() throws KettleException {
        if (databaseRepository == null) {
            databaseRepository = new KettleDatabaseRepository();
            DatabaseMeta databaseMeta = new DatabaseMeta("kt", repoDbVendor, "", repoHostname, repoDbName, repoDbPort, repoDbUsername, repoDbPassword);
            KettleDatabaseRepositoryMeta kettleDatabaseMeta = new KettleDatabaseRepositoryMeta("1", "kettle", "desc", databaseMeta);
            databaseRepository.init(kettleDatabaseMeta);
            databaseRepository.connect(repoUsername, repoPassword);
        }
    }

    private TransMeta getTransformationMeta(String transformationName) throws Exception {
/*
        TransMeta transMeta = transMetaCache.get(transformationName);
        if(transMeta != null) return transMeta;

        synchronized (this) {

            transMeta = transMetaCache.get(transformationName);
            if(transMeta != null) return transMeta;
*/

            RepositoryDirectoryInterface repoDirectory;
            String flowDirectory = FilenameUtils.getFullPath(transformationName);
            String flowFile = FilenameUtils.getBaseName(transformationName);
            if (flowDirectory == null || flowDirectory.trim().length() == 0) flowDirectory = "/";
            if (repositoryType.equals("DB")) {
                repoDirectory = databaseRepository.findDirectory(flowDirectory);
                return databaseRepository.loadTransformation(flowFile, repoDirectory, null, true, null);
            }
            repoDirectory = kettleFileRepository.findDirectory(flowDirectory);
            TransMeta transMeta = kettleFileRepository.loadTransformation(flowFile, repoDirectory, null, true, null);
//            transMetaCache.put(transformationName, transMeta);
            return transMeta;
//        }
    }

    public void init(){
        int maxConcurrentKTRFlows = 10;
        etlProcessingPermits = new Semaphore(maxConcurrentKTRFlows,true);
            run();
    }
    @Override
    public void run() {
        for(SupportedUploadFileTypes type: SupportedUploadFileTypes.values()) {
            filetypeToSigMaps.put(type, new ArrayList<SignatureOfEtlFlow>());
        }
        try {
            System.setProperty("KETTLE_DISABLE_CONSOLE_LOGGING",disableKettleConsoleLogging);
            KettleEnvironment.init(false);
            errorlistener = new HexKettleLoggingPlugin();
            KettleLogStore.getAppender().addLoggingEventListener(errorlistener);
            if (repositoryType.equals("FS")) {
                initFileRepository();
            } else {
                initDatabaseRepository();
            }
            parseRepo();
            TransMeta jsErrorTransMeta = getTransformationMeta("GenericJSErrorHandler");
            for (StepMeta stepMeta : jsErrorTransMeta.getSteps()) {
                if(stepMeta.getTypeId().equalsIgnoreCase("ScriptValueMod") && stepMeta.isDoingErrorHandling()) {
                    commonStepErrorMeta = stepMeta.getStepErrorMeta();
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Error processing KTR 'GenericJSErrorHandler' - exceptions is {} ", e.getMessage());
            throw  new RuntimeException(e);
        }
    }



    @Override
	public List<SignatureOfEtlFlow> getEtlFlowsSignaturesForFileType(SupportedUploadFileTypes fileType) {
        List<SignatureOfEtlFlow> signatureOfEtlFlows = filetypeToSigMaps.get(fileType);
        if (signatureOfEtlFlows == null) signatureOfEtlFlows = new ArrayList<SignatureOfEtlFlow>();
        return signatureOfEtlFlows;
    }

    private boolean getSenseParamsFromETLFlow(SignatureOfEtlFlow sig, TransMeta meta) {
        sig.setDesc(meta.getDescription());
        try {
            sig.setEtlType(meta.getParameterDefault("HexUploadType"));
        } catch (UnknownParamException e) {
            logger.error("Transformation '{}' does not have the parameter HEX_UPLOAD_TYPE set - this transformation will not be included in the Hex File Sense operation  ", meta.getName());
            return false;
        }

        try {
            sig.setDisplayName(meta.getParameterDefault("HexDisplayName"));
        } catch (UnknownParamException e) {
            logger.debug("Transformation '{}' does not have the parameter 'HexDisplayName' set - will default to the flow name {}", meta.getName(), meta.getName());
            sig.setDisplayName(meta.getName());
        }

        try {
            sig.setFileNamePattern(meta.getParameterDefault("HexFileNamePattern"));
        } catch (UnknownParamException e) {
            // No Sense parameter - not an issue - just continue
            logger.info("Transformation '{}' does not have the HexFileNamePattern file sense parameter set", meta.getName());
        }
        for (String param : meta.listParameters()) {
            try {
                sig.getSenseParams().put(param, meta.getParameterDefault(param));
            } catch (UnknownParamException e) {
                //This will never happen
            }
        }
        return true;
    }

    private Map<String, String> ktr2DownloadFilename = new HashMap<>();
    public void parseRepo() {
        //FIXME if(repositoryType.equals("DB"))

        ObjectId rootDirectoryID = null;
        List<RepositoryElementMetaInterface> transformationObjects = null;

        try {
            rootDirectoryID = kettleFileRepository.getRootDirectoryID();
            transformationObjects = kettleFileRepository.getTransformationObjects(rootDirectoryID, false);
        } catch (KettleException e) {
            logger.error("Error accessing Kettle Repository. Continuing but NONE of the Uploads thru Kettle will be functional. Error is {}", e.getMessage());
            return;
        }

        List<String> flowNames  = _em.createQuery("select distinct fileSenseAuth.flowName from FileSenseAuth as fileSenseAuth where fileSenseAuth.isActive = 'Y'").getResultList();
        int index= 1;
        for (RepositoryElementMetaInterface r : transformationObjects) {
            if(!flowNames.contains(r.getName()))  continue;
            if (r.getObjectType().equals(RepositoryObjectType.TRANSFORMATION)) {
                logger.trace("Processing flow {}", r.getName());
                TransMeta transMeta = null;
                try {
                    transMeta = kettleFileRepository.loadTransformation(r.getName(), r.getRepositoryDirectory(), null, true, null);
                } catch (KettleException e){
                    logger.error("Exception in parsing flow '{}' - this flow will be excluded. Error is {}", r.getName(), e.getMessage());
                    continue;
                }

                try {
                    String hexDownloadFileName = transMeta.getParameterDefault("HexDownloadFileName");
                    if(hexDownloadFileName != null){
                        ktr2DownloadFilename.put(r.getName().toUpperCase() + ".KTR", hexDownloadFileName);
                        continue;
                    }
                } catch (UnknownParamException e) {
                    //Nothing to do - this is not a KTR meant for KTR based Reports
                }

                StepMeta inputMeta = transMeta.findStep("Input_Node");
                if (inputMeta == null) {
                    logger.error("Transformation {} does not have an step called 'Input_Node' - the input step of every transformation MUST have this step.", r.getName());
                    continue;
                }
                if(transMeta.findStep("Output_Node") == null){
                    logger.error("Transformation {} does not have a step called 'Output_Node' - every transformation MUST have this step.", r.getName());
                    continue;
                }

                SignatureOfEtlFlow signatureOfEtlFlow = null;
                SupportedUploadFileTypes fileType;
                boolean isRegex = false;
                if (inputMeta.getStepID().equals("ExcelInput")) {
                    ExcelSignature signature = new ExcelSignature();
                    signature.setEtlFlowName(r.getName());
                    if (getSenseParamsFromETLFlow(signature, transMeta) == false) continue;
                    String hexSenseParamExcel = signature.getSenseParams().get("HexSenseParamExcel");
                    signature.setSpreadSheetType(((ExcelInputMeta)inputMeta.getStepMetaInterface()).getSpreadSheetType());
                    if(hexSenseParamExcel == null){
                        hexSenseParamExcel  = signature.getSenseParams().get("HexSenseParamExcelRegex");
                        isRegex = true;
                        if(hexSenseParamExcel != null){
                            logger.debug("Flow {} is of type excel and has a REGEX pattern", r.getName());
                        }
                    }
                    if(hexSenseParamExcel != null){
                        ExcelInputMeta excelInputMeta = (ExcelInputMeta) inputMeta.getStepMetaInterface();
                        String sheetName =  ((excelInputMeta.getSheetName().length == 0)? null:excelInputMeta.getSheetName()[0]);
                        signature.setSheetname(sheetName);

                        String[] split = hexSenseParamExcel.split(",");
                        if((split.length % 3) != 0){
                            logger.error("HexSenseParamExcel is set for flow {} but the format is invalid - value set in flow is {}", r.getName(), hexSenseParamExcel);
                            continue;
                        }
                        try {
                            for(int i = 0; i < split.length/3; i++){
                                signature.addPattern(Integer.parseInt(split[i*3].trim()), Integer.parseInt(split[1+(i*3)].trim()),split[2+(i*3)]);
                                logger.trace("For flow {} - filesense is '{}'", new String[]{r.getName(), hexSenseParamExcel});
                            }
                        }catch (NumberFormatException e){
                            logger.error("HexSenseParamExcel is set for flow {} but the number format is invalid - value set in flow is {}", r.getName(), hexSenseParamExcel);
                            continue;
                        }
                        signature.setPatternRegex(isRegex);
                    } else {
                        logger.trace("Flow {} does not have the HexSenseParamExcel parameter set - the field names will be used instead", r.getName());
                        ExcelInputMeta e = (ExcelInputMeta) inputMeta.getStepMetaInterface();
                        ExcelInputField[] fields = e.getField();
                        if (fields.length > 0) {
                            for(int i = 0; i < fields.length; i++){
                                ExcelInputField field = fields[i];
                                int startColumn = ((e.getStartColumn().length == 0)? 0:e.getStartColumn()[0]);
                                int startRow = ((e.getStartRow().length == 0)? 0:e.getStartRow()[0]);
                                signature.addPattern(startRow, startColumn+i, field.getName());
                                logger.trace("Fld name is {} at row {}, col {}", new String[]{fields[i].getName(),startRow+"", ""+startColumn+i});
                            }
                            String sheetName =  ((e.getSheetName().length == 0)? null:e.getSheetName()[0]);
                            signature.setSheetname(sheetName);
                        } else {
                            logger.error("Flow {} has not Regex Set not does the input node have any fields - this flow will be discarded", r.getName());
                            continue;
                        }
                        signature.setPatternRegex(false);
                    }
//                    if(signature.getSpreadSheetType().equals(SpreadSheetType.JXL)) filetypeToSigMaps.get(SupportedUploadFileTypes.ExcelInput_PRE_2007).add(signature);
//                    if(signature.getSpreadSheetType().equals(SpreadSheetType.POI)) filetypeToSigMaps.get(SupportedUploadFileTypes.ExcelInput_2007Plus).add(signature);
                    fileType = SupportedUploadFileTypes.EXCEL;
                    signatureOfEtlFlow = signature;
                    logger.debug("Signature of Excel flow is {}", signature);
                } else if (inputMeta != null && inputMeta.getStepID().equals("XBaseInput")) {
                    DbfSignature signature = new DbfSignature();
                    signature.setEtlFlowName(r.getName());
                    if (!getSenseParamsFromETLFlow(signature, transMeta)) continue;
                    inputMeta = transMeta.findStep("DBF_Input_Node");
                    if(inputMeta == null){
                        logger.error("Transformation {} does not have an step called 'DBF_Input_Node' - every transformation that uses a DBF file as input MUST have this step.", r.getName());
                        continue;
                    }
                    for (String fldName : ((SelectValuesMeta) inputMeta.getStepMetaInterface()).getSelectName()) {
                        if (fldName.equalsIgnoreCase("upd_record_number")) continue;
                        signature.addColName(fldName.toUpperCase());
                    }
                    fileType = SupportedUploadFileTypes.XBaseInput;
                    signatureOfEtlFlow = signature;
                } else if (inputMeta != null && (inputMeta.getStepID().equals("getXMLData") ||inputMeta.getStepID().equals("CsvInput") || inputMeta.getStepID().equals("TextFileInput") || inputMeta.getStepID().equals("ScriptValueMod"))) {
                    TextSignature signature = new TextSignature();
                    signature.setEtlFlowName(r.getName());
                    if (getSenseParamsFromETLFlow(signature, transMeta) == false) continue;
                    String hexSenseParam = signature.getSenseParams().get("HexSenseParamText");
                    String hexSenseParamRegex = signature.getSenseParams().get("HexSenseParamTextRegex");
                    if(hexSenseParam != null){
                        String[] split = hexSenseParam.split(",", 2);
                        if(split.length != 2){
                            logger.error("HexSenseParamText is set for flow {} but the format is invalid - value set in flow is {}", r.getName(), hexSenseParam);
                            continue;
                        }
                        try {
                            signature.setLineNum(Integer.parseInt(split[0].trim()));
                        }catch (NumberFormatException e){
                            logger.error("HexSenseParamText is set for flow {} but the line number is invalid - value set in flow is {}", r.getName(), split[0]);
                            continue;
                        }
                        try {
                            signature.setContainsExpression(URLDecoder.decode(split[1], "UTF-8"));
                        } catch (Exception e) {
                            logger.error("Unable to decode contains pattern {} for flow {} - flow will be discarded. Error is {}", new String[]{split[1], r.getName(), e.getMessage()});
                            continue;
                        }
                        logger.debug("For flow {} - filesense is 'Line = {} and ContainsExpression = {}'", new String[]{r.getName(), signature.getLineNum()+"", signature.getContainsExpression()});
                    } else if(hexSenseParamRegex != null){
                        String[] split = hexSenseParamRegex.split(",", 2);
                        if(split.length != 2){
                            logger.error("HexSenseParamTextRegex is set for flow {} but the format is invalid - value set in flow is {}", r.getName(), hexSenseParam);
                            continue;
                        }
                        try {
                            signature.setLineNum(Integer.parseInt(split[0].trim()));
                        }catch (NumberFormatException e){
                            logger.error("HexSenseParamTextRegex is set for flow {} but the line number is invalid - value set in flow is {}", r.getName(), split[0]);
                            continue;
                        }
                            signature.setRegex(split[1]);
                        logger.trace("For flow {} - filesense is 'Line = {} and Regex = {}'", new String[]{r.getName(), signature.getLineNum()+"", signature.getRegex()});
                    } else {
                        logger.error("Flow {} does not have the HexSenseParamText or HexSenseParamTextRegex parameter set - this flow will be discarded", r.getName());
                        continue;
                    }
                    signatureOfEtlFlow = signature;
                    fileType = SupportedUploadFileTypes.TextInput;
                } else if (inputMeta != null) {
                    logger.warn("Ignoring non (EXCEL|CSV|SCRIPT|DBF) Trans = {}. Input step type is - {}", r.getName(), inputMeta.getStepID());
                    continue;
                } else {
                    logger.error("Unsupported Input Type {}. Transformation {} is rejected",inputMeta.getStepID(), r.getName());
                    continue;
                }

                Map<String, SignatureOfEtlFlow> configForType;
                String etlType = signatureOfEtlFlow.getEtlType();
                if(etlType == null){
                    logger.error("Transformation {} does not have the paramater 'HexUploadType' set- every transformation MUST have this parameter set. This transformation will beignored", r.getName());
                    continue;
                }
                if ((configForType = uploadTypeToDisplayNameMaps.get(etlType)) == null) {
                    configForType = new HashMap<String, SignatureOfEtlFlow>();
                    uploadTypeToDisplayNameMaps.put(etlType, configForType);
                }
                filetypeToSigMaps.get(fileType).add(signatureOfEtlFlow);
                flowToTypeMapping.put(signatureOfEtlFlow.getEtlFlowName(), signatureOfEtlFlow);
                flowToDispNameMaping.put(signatureOfEtlFlow.getEtlFlowName(), signatureOfEtlFlow.getDisplayName());
                logger.trace("{} Mapping flow {} to type {}", index++,signatureOfEtlFlow.getEtlFlowName(), etlType);
                configForType.put(signatureOfEtlFlow.getDisplayName(), signatureOfEtlFlow);
            }
        }
    }


    public static void main(String[] args) {
        try {
            String decode = URLDecoder.decode(".\\+?,\\d{1,2}\\W\\w{3}\\W\\d{4},\\d{1,2}\\W\\w{3}\\W\\d{4},\\w,.+?,.+?,.+?,.+?,.+?,.+?,.+?,.+?,.+?,.+?,.+?,.+?,.+?,\\w*$", "UTF-8");

            System.out.println(decode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
	public List<String> getEtlTypes() {
        Iterator<String> itr = uploadTypeToDisplayNameMaps.keySet().iterator();
        List<String> uploadTypes = new ArrayList<String>();
        while (itr.hasNext()) uploadTypes.add(itr.next());
        return uploadTypes;

    }

    @Override
	public Collection<SignatureOfEtlFlow> getEtlSigForType(String etlType) {
        Map<String, SignatureOfEtlFlow> etlFlowMap = uploadTypeToDisplayNameMaps.get(etlType);
        if (etlFlowMap == null) {
            logger.error("Invalid ETL Flow Type '{}' ", etlType);
            //FIXME - thorw exception
            return null;
        }
        return etlFlowMap.values();
    }

    @Override
	public SignatureOfEtlFlow getEtlSig(String etlType, String displayName) {
        Map<String, SignatureOfEtlFlow> etlFlowMap = uploadTypeToDisplayNameMaps.get(etlType);
        if (etlFlowMap == null) {
            logger.error("Invalid ETL Flow Type {} ", etlType);
            //FIXME - thorw exception
            return null;
        }
        SignatureOfEtlFlow signatureOfEtlFlow = etlFlowMap.get(displayName);
        return signatureOfEtlFlow;
    }

    @Override
    public String downloadData(String etlFlowName, Map<String, String> params) throws Exception{
        uploadData(null, null, etlFlowName, null, params);
        return ktr2DownloadFilename.get(etlFlowName.toUpperCase());
    }


    @Override
	public EtlJobStats uploadData(String uploadID, String fileName, String etlFlowName, String updTableName, Map<String, String> params) throws Exception {
        DatabaseInterface databaseInterface = null;
        EtlJobStats jobStats = new EtlJobStats();
        jobStats.setUploadId(uploadID);
        Trans trans = null;
        try {
            logger.trace("acquiring etlProcessingPermit");
            etlProcessingPermits.acquire();
            logger.trace("got etlProcessingPermit");
            TransMeta transformationMeta = getTransformationMeta(etlFlowName);



            /* added for JNDI FIX - FIXME */
          /*  if(HexConstants.POSTGRESQL_JPAVENDORADAPTOR.equals(jpavendoradaptor)){
                databaseInterface = new PostgreSQLDatabaseMeta();
                databaseInterface.setPluginId("POSTGRESQL");
                databaseInterface.setPluginName("PostgreSQL");
                databaseInterface.getAttributes().setProperty(PostgreSQLDatabaseMeta.ATTRIBUTE_SUPPORTS_TIMESTAMP_DATA_TYPE, "N");
                databaseInterface.getAttributes().setProperty(PostgreSQLDatabaseMeta.ATTRIBUTE_PRESERVE_RESERVED_WORD_CASE, "N");
            } else if(HexConstants.ORACLE_JPAVENDORADAPTOR.equals(jpavendoradaptor)){
                databaseInterface = new OracleDatabaseMeta();
                databaseInterface.setPluginId("ORACLE");
                databaseInterface.setPluginName("Oracle");
            } else {*/

           /* GenericDatabaseMeta genericDb = new GenericDatabaseMeta();
            genericDb.setName("Hexgen");
            genericDb.setPassword(password);
            genericDb.setUsername(dbusername);

            genericDb.getAttributes().setProperty(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, url);
            genericDb.getAttributes().setProperty(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, driverClassName);
            genericDb.setAccessType(DatabaseMeta.TYPE_ACCESS_NATIVE);*/
            databaseInterface = new MySQLDatabaseMeta();
            databaseInterface.setPluginId("GENERIC");
            databaseInterface.setPluginName("Generic database");
            databaseInterface.setPassword(password);
            databaseInterface.setUsername(dbusername);
            databaseInterface.getAttributes().setProperty(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, url);
            databaseInterface.getAttributes().setProperty(GenericDatabaseMeta.ATTRIBUTE_SUPPORTS_TIMESTAMP_DATA_TYPE, "N");
            databaseInterface.getAttributes().setProperty(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, driverClassName);
            databaseInterface.getAttributes().setProperty(GenericDatabaseMeta.ATTRIBUTE_PRESERVE_RESERVED_WORD_CASE, "N");
//            }
            databaseInterface.setName("HexGenJNDI");
            databaseInterface.setDatabaseName("HexGenJNDI");
            databaseInterface.setAccessType(DatabaseMeta.TYPE_ACCESS_JNDI);
            databaseInterface.getAttributes().setProperty(GenericDatabaseMeta.ATTRIBUTE_FORCE_IDENTIFIERS_TO_LOWERCASE, "N");
            databaseInterface.getAttributes().setProperty(GenericDatabaseMeta.ATTRIBUTE_FORCE_IDENTIFIERS_TO_UPPERCASE, "N");
            databaseInterface.getAttributes().setProperty(GenericDatabaseMeta.ATTRIBUTE_IS_CLUSTERED, "N");
            databaseInterface.getAttributes().setProperty(GenericDatabaseMeta.ATTRIBUTE_QUOTE_ALL_FIELDS, "N");
            databaseInterface.getAttributes().setProperty(GenericDatabaseMeta.ATTRIBUTE_SUPPORTS_BOOLEAN_DATA_TYPE, "N");
            databaseInterface.getAttributes().setProperty(GenericDatabaseMeta.ATTRIBUTE_USE_POOLING, "Y");

            StepMeta errorHandlerStep = (StepMeta)commonStepErrorMeta.getTargetStep().clone();
            errorHandlerStep.setName(errorHandlerStep.getName() + 420);
            errorHandlerStep.setParentTransMeta(transformationMeta);

            for (StepMeta stepMeta : transformationMeta.getSteps()) {

                if(stepMeta.getTypeId().equalsIgnoreCase("ScriptValueMod") && !stepMeta.isDoingErrorHandling()) {
                    StepErrorMeta stepErrorMeta = commonStepErrorMeta.clone();
                    stepErrorMeta.setTargetStep(errorHandlerStep);
                    stepErrorMeta.setEnabled(true);
                    stepErrorMeta.setErrorCodesValuename("errCode");
                    stepErrorMeta.setErrorDescriptionsValuename("errDesc");
                    stepErrorMeta.setErrorFieldsValuename("errField");
                    stepErrorMeta.setNrErrorsValuename("errNum");
                    stepErrorMeta.setSourceStep(stepMeta);
                    stepMeta.setStepErrorMeta(stepErrorMeta);

                    TransHopMeta errorHop = new TransHopMeta(stepMeta,stepErrorMeta.getTargetStep());
                    transformationMeta.addTransHop(errorHop);
                }
                if(stepMeta.getTypeId().equalsIgnoreCase("Tableoutput")) {
                    TableOutputMeta tableOutputMeta = (TableOutputMeta) stepMeta.getStepMetaInterface();
                    tableOutputMeta.getDatabaseMeta().setDatabaseInterface(databaseInterface);
                    if (!(Integer.parseInt(tableOutputMeta.getCommitSize()) > 1)) tableOutputMeta.setCommitSize(1);
                    tableOutputMeta.setUseBatchUpdate(false);
                }
                if(stepMeta.getTypeId().equalsIgnoreCase("DBLookup")) {
                    DatabaseLookupMeta tableOutputMeta = (DatabaseLookupMeta) stepMeta.getStepMetaInterface();
                    if(tableOutputMeta.getDatabaseMeta() != null) {
                        tableOutputMeta.getDatabaseMeta().setDatabaseInterface(databaseInterface);
                    }
                }
                if(stepMeta.getTypeId().equalsIgnoreCase("Tableinput")) {
                    TableInputMeta tableInputMeta = (TableInputMeta) stepMeta.getStepMetaInterface();
                    tableInputMeta.getDatabaseMeta().setDatabaseInterface(databaseInterface);

                }
            }

            transformationMeta.addStep(errorHandlerStep);

            /* END of JNDI FIX - FIXME */
            trans = new Trans(transformationMeta);
            if(updTableName != null){
                TableOutputMeta tableNode = (TableOutputMeta) transformationMeta.findStep("Output_Node").getStepMetaInterface();
                tableNode.setTablename(updTableName);
            }

            trans.setParameterValue("HexInputFileName", fileName);
            try {
                trans.addParameterDefinition("HexUploadID", uploadID, null);
                for (String paramName : params.keySet()) {
                    try {
                        trans.addParameterDefinition(paramName, params.get(paramName), null);
                    }catch (DuplicateParamException e ){
                        trans.setParameterValue(paramName, params.get(paramName));
                    }
                }
            } catch (DuplicateParamException e) {
                logger.info("Transformation '{}' already has HexUploadID as a parameter - probably harmless but it shouldn't have this parameter defined!!", etlFlowName);
            }
            trans.setParameterValue("HexUploadID", uploadID);
            trans.setLogLevel(LogLevel.DEBUG);
            transformationMeta.setLogLevel(LogLevel.DEBUG);
            trans.prepareExecution(null);
            trans.startThreads();

            // TODO - Does the call below ensure that all transformations are
            // complete??
            trans.waitUntilFinished();

            if (trans.getErrors() > 0) {
                // TODO make this an hexgen exception
                throw new RuntimeException("Kettle Errors while processing flow with id: " + uploadID + " " + errorlistener.getKettleErrors(trans));
            }

            List<StepMetaDataCombi> steps = trans.getSteps();
            for (StepMetaDataCombi s : steps) {
                if (s.stepname.contains("Input_Node") && !s.stepname.startsWith("DBF_")) {
                    jobStats.setNumberOfLinesInput(jobStats.getNumberOfLinesInput().add(new BigDecimal(s.step.getLinesWritten())));

                } else if (s.stepname.contains("Output_Node")) {
                    jobStats.setNumberOfLinesOutput(jobStats.getNumberOfLinesOutput().add(new BigDecimal(s.step.getLinesWritten())));

                } else if (s.stepname.contains("Error_Node") || s.stepname.contains("Persist Errors")) {
                    jobStats.setNumberOfErrors( jobStats.getNumberOfErrors().add(new BigDecimal(s.step.getLinesWritten())));

                } else if (s.stepname.contains("Sink_Node")) {
                    jobStats.setNumberOfRejected(jobStats.getNumberOfRejected().add(new BigDecimal(s.step.getLinesWritten())));
                }
            }

        } catch (Exception e) {
            logger.error("error during etl",e);
            throw e;
        } finally {
            try {
                if(trans != null) {
                    errorlistener.clearErrors(trans);
                    trans.cleanup();
                }
            } catch (Exception e) {
                //do nothing
            }
            logger.trace("releasing etlProcessingPermit");
            etlProcessingPermits.release();
        }
        return jobStats;
    }

    @Override
	public List<SignatureOfEtlFlow> senseFileType(String filename, List<String> passwords)
    {
        return hexFileSense.senseFileType(filename, null,passwords);
    }


    @Override
    public String getParameterForFlow(String flowName, String param) {
        SignatureOfEtlFlow signatureOfEtlFlow = flowToTypeMapping.get(flowName);
        if(signatureOfEtlFlow == null){
            logger.error("Requested parameter '{} for a flow, '{}' that does not exist", param, flowName);
            return null;
        }
        String s = signatureOfEtlFlow.getSenseParams().get(param);
        if(s == null){
            logger.error("Requested parameter '{} does not exist for flow, '{}'", param, flowName);
        }
        return s;

    }
    @Override
    public String getEtlTypeForFlow(String flowName) {
        return flowToTypeMapping.get(flowName).getEtlType();
    }
    @Override
    public String getDisplayNameForFlow(String flowName) {
        return flowToDispNameMaping.get(flowName);
    }
    @Override
    public SignatureOfEtlFlow getSignatureForFlow(String flowname){
        return flowToTypeMapping.get(flowname);
    }

    @Override
    public boolean busy() {
        if (etlProcessingPermits.hasQueuedThreads())
            return true;
        return etlProcessingPermits.availablePermits() >=2;
    }

}
