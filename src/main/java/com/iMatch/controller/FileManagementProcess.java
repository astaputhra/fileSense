package com.iMatch.controller;

import com.iMatch.AbstractEtlMonitor;
import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.EtlErrors;
import com.iMatch.etl.EtlException;
import com.iMatch.etl.helperClasses.FileUtilities;
import com.iMatch.etl.internal.UploadStatus;
import com.iMatch.etl.orm.IFileSenseAuth;
import com.iMatch.etl.orm.IUploadJobMaster;
import com.iMatch.etl.orm.UploadJobMaster;
import com.iMatch.etl.preprocessmethods.PreProcessTime;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Astaputhra on 08-04-2020.
 */
public class FileManagementProcess extends AbstractEtlMonitor {

    private static final Logger logger = LoggerFactory.getLogger(FileManagementProcess.class);
    private class PreProcessInputs {
        EtlDefinition etlDefinition;
        PreProcessTime[] timeInputs;

        public PreProcessInputs(EtlDefinition etlDefinition, PreProcessTime[] timeInputs) {
            this.etlDefinition = etlDefinition;
            this.timeInputs = timeInputs;
        }
    }

    private Map<File,FileManagementProcess.PreProcessInputs> preProcessMap = new ConcurrentHashMap<>();

    public boolean preProcess(File file) {
        logger.debug("pre-process-hook file {}, size {}, last-modified {}", file.getName(),file.length(),new Date(file.lastModified()));
        FileManagementProcess.PreProcessInputs preProcessInputs = preProcessMap.get(file);
        File tmpFile;
        if (preProcessInputs == null) {
            EtlDefinition etlDefn = null;
            try {
                tmpFile = getTempFile(file);
                String[] companyAndDivision = getCompanyAndDivisionAndFileName(file.getAbsolutePath());
                etlDefn = getEtlDefinitionForFile(tmpFile, companyAndDivision[0], companyAndDivision[1]);
                if (etlDefn == null) return false;
            } catch (AbstractEtlMonitor.EtlMonitorException e) {
                // Do nothing - handler will do the appropriate thing
                return false;
            } catch (Exception e) {
                logger.error("pre-process-hook, error occurred for file " + file.getName(),e);
                return false;
            }

            UploadJobMaster uploadJobMaster = new UploadJobMaster();
            String checksumSHA1=null;

            try {
                checksumSHA1 = FileUtilities.checksumSHA1(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<UploadJobMaster> byChecksum = iUploadJobMaster.findByChecksum(uploadJobMaster.getChecksum());

            uploadJobMaster = uploadJobMaster.populateUploadJobMaster(etlDefn,file.getName(),configuredCompany,configuredDivision,checksumSHA1);
            uploadJobMaster.setFilename(tmpFile.getPath());
            iUploadJobMaster.save(uploadJobMaster);
            iUploadJobMaster.flush();

            logger.trace("Generating upload id for upload job master entry");
            int id = uploadJobMaster.getId().intValue();
            uploadJobMaster.setUploadId(etlDefn.getEtlFlow() + id + DateTimeFormat.forPattern("ddMMyyyy").print(new LocalDate()));

                if(CollectionUtils.isNotEmpty(byChecksum)){
                    uploadJobMaster.setStatus(UploadStatus.DUPLICATE_FILE.toString());
                    iUploadJobMaster.save(uploadJobMaster);
                    iUploadJobMaster.flush();
                    etlManager.populateImEventLog(uploadJobMaster);
                    logger.debug("Since The File Is Duplicate returning the call");
                    return true;
                }

            iUploadJobMaster.save(uploadJobMaster);
            iUploadJobMaster.flush();

            if(etlDefn.getErrorType() == null){
                try {
                    etlManager.runEtlFlow(uploadJobMaster,configuredCompany,configuredDivision);
                } catch (EtlException e) {
                    e.printStackTrace();
                } catch (EtlErrors etlErrors) {
                    etlErrors.printStackTrace();
                }

            }
        }
        return true;
    }
}
