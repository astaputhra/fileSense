package com.iMatch;

import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.datauploader.internal.hexFileSense.SignatureOfEtlFlow;
import com.iMatch.etl.enums.UploadErrorType;
import com.iMatch.etl.orm.IUploadJobMaster;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.file.filters.LastModifiedFileListFilter;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

//import org.apache.commons.lang3.StringUtils;

public abstract class AbstractEtlMonitor extends LastModifiedFileListFilter {

    @Value("#{appProp['etl.directory.monitor.directory']}")
    protected String root;

    @Autowired
    protected IUploadJobMaster iUploadJobMaster;

    @Value("#{appProp['etl.directory.scannerResetThreshold']}")
    int scannerResetThreshold;

    @Value("#{appProp['etl.directory.processed']}")
    protected String processedDir;

    @Value("#{appProp['etl.directory.monitor.company']}")
    protected String configuredCompany;

    @Value("#{appProp['etl.directory.monitor.division']}")
    protected String configuredDivision;

    @Value("#{appProp['etl.directory.tmpFolder']}")
    protected String tmpFolder;

    private boolean isCompanyAndDivisionPartOfPath = false;

    private static final Logger logger = LoggerFactory.getLogger(AbstractEtlMonitor.class);

    protected String fileSeparator = System.getProperty("file.separator");

    @Autowired
    protected EtlManager etlManager;

    @PersistenceContext(unitName = "entityManagerFactory")
    protected EntityManager _em;

    private boolean ready = false;

    @Value("#{appProp['etl.blacklistedFilesRegex']}")
    private String blacklistedFilesRegex;
    private Pattern blacklistPattern;

    public EtlDefinition getEtlDefinitionForFile(File file, String company, String division)  throws EtlMonitorException {
        EtlMonitorException e = new EtlMonitorException();
        EtlDefinition etlDefinition = null;
        e.getErrAttrs().put("unknownFile", file.getName());
        List<SignatureOfEtlFlow> signatureOfEtlFlows = null;
        try {
            signatureOfEtlFlows = etlManager.senseFileType(file.getAbsolutePath(), etlManager.getPasswords(company));
        } catch (Exception ex){
//            if(ex instanceof HexGenSystemError && SystemErrorMessages.ETL_PASSWORD_MISMATCH.getMessageCode().equals(((HexGenSystemError)ex).getMessageCode())){
//                e.setErrorType(UploadErrorType.PASSWORD_EXCEPTION);
//                throw e;
//            }
//            e.setErrorType(UploadErrorType.SYSTEM_ERROR);
            throw new RuntimeException(ex.getMessage());
        }

        if(signatureOfEtlFlows.size() != 1){
            if(signatureOfEtlFlows.isEmpty()){
                logger.error("Unable to determine which flow matches file '{}'. This cannot be processed.", file.getAbsolutePath());
                e.setErrorType(UploadErrorType.NO_MAPPING_FLOW);
                etlDefinition = new EtlDefinition();
                etlDefinition.setErrorType(UploadErrorType.NO_MAPPING_FLOW);

                return etlDefinition;
//                throw e;
            }
            String s = "";
            for(SignatureOfEtlFlow sig: signatureOfEtlFlows){
                s = s.concat(sig.getEtlFlowName() + "::");
            }
            e.setErrorType(UploadErrorType.MULTIPLE_MAPPING_FOUND);
            e.getErrAttrs().put("listOfDupMappings", s);
            logger.error("Multiple flows matches file '{}' - cannot be processed. Matched flows are {}", file.getAbsolutePath(), s);
            etlDefinition = new EtlDefinition();
            etlDefinition.setErrorType(UploadErrorType.MULTIPLE_MAPPING_FOUND);

            return etlDefinition;
//            throw e;
        }
        SignatureOfEtlFlow sig = signatureOfEtlFlows.get(0);
        etlDefinition = etlManager.getEtlDefinitionForFlow(sig.getEtlFlowName(), company, division);
        if(etlDefinition == null){
            logger.error("Unable to find Entry in Table 'File Sense Auth' for flow {}, company {} and division {}.", new String[]{sig.getEtlFlowName(), company, division});
            e.setErrorType(UploadErrorType.NO_LEAF_FOUND);
            etlDefinition = new EtlDefinition();
            etlDefinition.setErrorType(UploadErrorType.NO_LEAF_FOUND);
            e.getErrAttrs().put("flowname", sig.getEtlFlowName());
            return etlDefinition;
//            throw e;
        }
        etlDefinition.setGenericType(sig.getEtlType());
        etlDefinition.setDisplayName(sig.getDisplayName());
        return etlDefinition;
    }
    public class EtlMonitorException extends Exception{
        private static final long serialVersionUID = -7696213279029952957L;
        private UploadErrorType errorType;
        Map<String, String> errAttrs = new HashMap<String, String>();

        public UploadErrorType getErrorType() {
            return errorType;
        }

        public void setErrorType(UploadErrorType errorType) {
            this.errorType = errorType;
        }

        public Map<String, String> getErrAttrs() {
            return errAttrs;
        }

        public void setErrAttrs(Map<String, String> errAttrs) {
            this.errAttrs = errAttrs;
        }
    }
    protected List<UnzippedFile> unzip(InputStream inputStream, String name)
    {
        byte[] buffer = new byte[1024];
        List<UnzippedFile> files = null;
        if(inputStream == null)return files;
        ZipInputStream zip = null;
        FileOutputStream fileOutputStream = null;
        File tmpFile = null;
        boolean isFromEmail = false;
        try {
            ZipEntry entry;
            tmpFile = FileUtils.getFile(FileUtils.getTempDirectory(),name);
            if (tmpFile == null || !tmpFile.exists()) {
                isFromEmail = true;
                tmpFile = File.createTempFile(name, "zip");
                fileOutputStream = new FileOutputStream(tmpFile);
                int read = 0;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, read);
                }
            }
            ZipFile zipFile = new ZipFile(tmpFile);
            if (zipFile.isEncrypted()) {
                List<UnzippedFile> unzippedFiles = processPasswordProtectedZip(zipFile, tmpFile.getParentFile().getAbsolutePath(), getPasswordsForExtension());
                if (isFromEmail) {
                    fileOutputStream.close();
                    tmpFile.delete();
                }
                return unzippedFiles;
            }

            FileInputStream inptStream = new FileInputStream(tmpFile);
            zip = new ZipInputStream(inptStream);
            files = new ArrayList<UnzippedFile>();
            while ((entry = zip.getNextEntry()) != null) {
                String fileName = entry.getName();
                if(entry.isDirectory() || fileName.endsWith(fileSeparator) || isFileBlacklisted(fileName)){
                    continue; //This is a directory
                }

                File file = File.createTempFile(FilenameUtils.getBaseName(fileName), "." + FilenameUtils.getExtension(fileName));
                FileOutputStream fos = new FileOutputStream(file);

                int len;
                while ((len = zip.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                UnzippedFile unzippedFile = new UnzippedFile();
                unzippedFile.file = file;
                unzippedFile.originalfilename = fileName;
                files.add(unzippedFile);
            }
        }catch (IOException e){
            logger.debug("Error unzipping file " + name + ". Error is " + e.getMessage());
        } catch (ZipException e) {
            logger.debug("Error unzipping file " + name + ". Error is " + e.getMessage());
        }finally {
            IOUtils.closeQuietly(zip);
            IOUtils.closeQuietly(fileOutputStream);
            if (isFromEmail) tmpFile.delete();
        }
        return files;
    }


    public class UnzippedFile{
        public File file;
        public String originalfilename;
    }


    private List<UnzippedFile>  processPasswordProtectedZip(ZipFile zipFile, String tmpDir, List<String> passwords){
        List<UnzippedFile>  files = new ArrayList<UnzippedFile>();
        for (String password : passwords) {
            try {
                zipFile.setPassword(password);
                for (FileHeader o : (List<FileHeader>)zipFile.getFileHeaders()) {
                    if (isFileBlacklisted(o.getFileName())) {
                        continue;
                    }
                    zipFile.extractFile(o, tmpDir);
                    if(o.isDirectory() || o.getFileName().endsWith(fileSeparator)){
                        continue; //This is a directory
                    }

                    File file = new File(FilenameUtils.concat(tmpDir, o.getFileName()));
                    if(file == null) continue;
                    UnzippedFile unzippedFile = new UnzippedFile();
                    unzippedFile.file = file;
                    unzippedFile.originalfilename =  o.getFileName();
                    files.add(unzippedFile);
                }
            }catch (ZipException e){
                if(e.getMessage().contains("Wrong Password")) {
                    if(password.equals(passwords.get(passwords.size()-1))){
                        throw new RuntimeException("Incorrect Password");
                    }
                    continue;
                }
            }
        }
        return files;
    }

    private List<String> getPasswordsForExtension(){

        Query query = _em.createQuery("select DISTINCT fileSenseAuth.zipPassword from FileSenseAuth as fileSenseAuth WHERE fileSenseAuth.isActive = 'Y' and fileSenseAuth.zipPassword is not null ");
        List<String> resultList = query.getResultList();

        return resultList;

    }

    protected boolean isFileBlacklisted(String filename) {
        if (StringUtils.isEmpty(blacklistedFilesRegex))
            return false;
        else {
            if (blacklistPattern == null )
                blacklistPattern = Pattern.compile(blacklistedFilesRegex);
        }
        String name = FilenameUtils.getName(filename);
        Matcher matcher = blacklistPattern.matcher(name);
        boolean blacklisted = matcher.matches();
        if (blacklisted)
            logger.warn("File received for etl - {} is blacklisted and not processed", filename);
        return blacklisted;
    }

    public File getTempFile(File file) throws Exception {
        logger.trace("creating temp file for {}, size {}, last-modified", file.getName(), file.length(), file.lastModified());
        File tmpFile = createTmpFile(file);
        FileUtils.copyFile(file, tmpFile, false);
        logger.trace("created temp file for {}, temp-file {}, size {}", file.getName(), tmpFile.getName(),tmpFile.length());
        return tmpFile;
    }

    public static File createTmpFile(File file) throws IOException{
        File tmpFile;
        String baseName = FilenameUtils.getBaseName(file.getName());
        if(baseName.length() < 3) baseName += "xyz";
        tmpFile = File.createTempFile(baseName, "." + FilenameUtils.getExtension(file.getName()));
        return tmpFile;
    }

    protected String[] getCompanyAndDivisionAndFileName(String copiedPath) {
        String company = null;
        String division = null;
        String fullRoot = root + fileSeparator;
        String fName = copiedPath.substring(fullRoot.length());
        if (isCompanyAndDivisionPartOfPath) {
            String unixFilename = FilenameUtils.separatorsToUnix(fName);
            String[] split = unixFilename.split("/");
            if (split.length >= 3){
                company = split[split.length-3];
                division = split[split.length-2];
            }else if(split.length == 2){
                company = split[0];
            }
        } else {
            company = configuredCompany;
            division = configuredDivision;
        }
        return new String[]{company,division,fName};
    }
}
