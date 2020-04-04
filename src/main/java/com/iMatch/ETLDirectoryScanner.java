package com.iMatch;

import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.EtlErrors;
import com.iMatch.etl.EtlException;
import com.iMatch.etl.helperClasses.FileUtilities;
import com.iMatch.etl.orm.UploadJobMaster;
import com.iMatch.etl.preprocessmethods.PreProcessTime;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.file.WatchServiceDirectoryScanner;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Astaputhra on 17-03-2020.
 */
public class ETLDirectoryScanner extends AbstractEtlMonitor {

//    AbstractEtlMonitor directoryMonitor;

    @Value("#{appProp['etl.directory.monitor.directory']}")
    String root;

    @Value("#{appProp['etl.directory.scannerResetThreshold']}")
    int scannerResetThreshold;

    @Value("#{appProp['etl.directory.processed']}")
    private String processedDir;

    @Value("#{appProp['etl.directory.monitor.company']}")
    private String configuredCompany;

    @Value("#{appProp['etl.directory.monitor.division']}")
    private String configuredDivision;

    private boolean isCompanyAndDivisionPartOfPath = false;

    protected String fileSeparator = System.getProperty("file.separator");
    private static final Logger logger = LoggerFactory.getLogger(ETLDirectoryScanner.class);
    private String enabled;

    private ArrayList<File> pendingFiles = new ArrayList<>();

    private WatchServiceDirectoryScanner leafScanner;
    private long timeLastFilesReceived;


    boolean resetScannerEnabled() {
        return scannerResetThreshold >= 60;
    }

    public WatchServiceDirectoryScanner getLeafScanner() {
        return leafScanner;
    }

    public void setLeafScanner(WatchServiceDirectoryScanner leafScanner) {
        this.leafScanner = leafScanner;
    }

    private boolean isCompletelyWritten(File file) {
        RandomAccessFile stream = null;
        try {
            logger.debug("Checking if {} is completely written, exists {}", file.getName(), file.exists());
            if (!file.exists()) return true;
            stream = new RandomAccessFile(file, "rw");
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (stream != null) {
                IOUtils.closeQuietly(stream);
            }
        }
    }

    public void resetScanner() {
        System.out.println("restarting directory watch service");
        if(leafScanner == null) leafScanner = new WatchServiceDirectoryScanner(root); // FIXME
        leafScanner.stop();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            nothing
//        }
        leafScanner.start();
        System.out.println("directory watch service restarted");
    }

    public void checkScanner(int noOfFilesReceived) {
        if (resetScannerEnabled()) {
                logger.debug("No files received - {}", noOfFilesReceived);
                long timeSinceFilesLastReceived = System.currentTimeMillis() - timeLastFilesReceived;
                if (timeSinceFilesLastReceived >= (scannerResetThreshold * 1000)) {
                    logger.debug("Time since files last received {} millis is more than configured threshold for scanner reset", timeSinceFilesLastReceived);
                    Collection<File> filesOnDisk = FileUtils.listFiles(new File(root), null, true);
                    if (filesOnDisk.size() > 0) {
                        logger.debug("No of files in the dir {} ", filesOnDisk.size());
                        filesOnDisk.clear();
                        resetScanner();
                    } else {
                        logger.debug("No files in the dir ");
                    }
//                    timeLastFilesReceived = System.currentTimeMillis();
                }
        } else {
            logger.debug("scanner reset function not enabled");
        }
    }

    @Override
    synchronized public List<File> filterFiles(File[] newFiles) {
        logger.debug("newFiles " + Arrays.asList(newFiles));

        checkScanner(newFiles.length);

        List<File> newFilesToProcess = Stream.of(newFiles)
                .peek(f -> {
                    logger.debug("{} exists {}", f.getName(),f.exists());
                    logger.debug("{} isFIle {}",f.getName(), f.isFile());
                    logger.debug("{} isDirectory {}",f.getName(), f.isDirectory());
                })
                .filter(File::isFile).collect(Collectors.toList());
        logger.debug("new files to process " + newFilesToProcess);

        List<File> filesToProcess = new ArrayList<>();
        //files from previous poll
        filesToProcess.addAll(pendingFiles); // these get processed first
        filesToProcess.addAll(newFilesToProcess); // then the new files

        pendingFiles.clear();
        pendingFiles.addAll(filesToProcess);

        File[] filesToFilter = new File[filesToProcess.size()];
        List<File> filteredFiles = super.filterFiles(pendingFiles.toArray(filesToFilter));
        logger.debug("files filtered based on age " + filteredFiles);

        //if a file is not completely written or to be preProcessed, filter it out
        List<File> incompleteFiles = filteredFiles.stream()
                .filter(file -> !isCompletelyWritten(file) || preProcess(file))
                .collect(Collectors.toList());
        logger.debug("files incompletely written or to be pre-processed will be re-processed in next poll = " + incompleteFiles);
        filteredFiles.removeAll(incompleteFiles);

        pendingFiles.removeAll(filteredFiles);
        logger.debug("files pending after this poll " + pendingFiles);
        logger.debug("files that will be processed  " + filteredFiles);

        return filteredFiles;
    }

    private class PreProcessInputs {
        EtlDefinition etlDefinition;
        PreProcessTime[] timeInputs;

        public PreProcessInputs(EtlDefinition etlDefinition, PreProcessTime[] timeInputs) {
            this.etlDefinition = etlDefinition;
            this.timeInputs = timeInputs;
        }
    }
    private Map<File,PreProcessInputs> preProcessMap = new ConcurrentHashMap<>();

    private boolean preProcess(File file) {
        logger.trace("pre-process-hook file {}, size {}, last-modified {}", file.getName(),file.length(),new Date(file.lastModified()));
        PreProcessInputs preProcessInputs = preProcessMap.get(file);
        if (preProcessInputs == null) {
            EtlDefinition etlDefn = null;
            try {
                File tmpFile = getTempFile(file);
                String[] companyAndDivision = getCompanyAndDivisionAndFileName(file.getAbsolutePath());
                etlDefn = getEtlDefinitionForFile(tmpFile, companyAndDivision[0], companyAndDivision[1]);
                if (etlDefn == null) return false;
//                PreProcessTime[] preProcessTimes = PreProcessTimeFactory.getTimeInputs(etlDefn.getPreProcessMethod(),etlDefn.getPreProcessInput());
//                preProcessInputs = new PreProcessInputs(etlDefn, preProcessTimes);
//                preProcessMap.put(file, preProcessInputs);
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

           uploadJobMaster = uploadJobMaster.populateUploadJobMaster(etlDefn,file.getName(),configuredCompany,configuredDivision,checksumSHA1);
            uploadJobMaster.setFilename(file.getPath());
            uploadJobMaster.setFilename(file.getName());

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
        //and then apply
        EtlDefinition etlDefinition = preProcessInputs.etlDefinition;
        logger.debug("pre-process-hook - applying method {} with inputs {} for file {}", etlDefinition.getPreProcessMethod().name(), etlDefinition.getPreProcessInput(),file.getName());

        return true;
    }


    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getEnabled() {
        return enabled;
    }

    @PostConstruct
    public void init(){
        if (!"Y".equals(enabled)) {
            setAge(5);
        }
    }

    String[] getCompanyAndDivisionAndFileName(String copiedPath) {
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
