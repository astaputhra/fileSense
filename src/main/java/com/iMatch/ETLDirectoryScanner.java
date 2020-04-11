package com.iMatch;

import com.iMatch.controller.FileManagementProcess;
import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.helperClasses.FileUtilities;
import com.iMatch.etl.internal.UploadStatus;
import com.iMatch.etl.orm.UploadJobMaster;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.file.WatchServiceDirectoryScanner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Astaputhra on 17-03-2020.
 */
@Transactional
public class ETLDirectoryScanner extends AbstractEtlMonitor {

//    AbstractEtlMonitor directoryMonitor;

    @Autowired
    EtlDirectoryMonitor handler;

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

    @Value("#{appProp['etl.directory.tmpFolder']}")
    private String tmpFolder;

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
        if(leafScanner == null) leafScanner = new WatchServiceDirectoryScanner(root); // FIXME
        leafScanner.stop();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
//            nothing
        }
        leafScanner.start();
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
                .filter(file -> !isCompletelyWritten(file) || validateTheFile(file))
                .collect(Collectors.toList());
        filteredFiles.removeAll(incompleteFiles);

        pendingFiles.removeAll(filteredFiles);
        return filteredFiles;
    }

    private boolean validateTheFile(File file) {
        File tmpFile = null;
        try {
            tmpFile = getTempFile(file);
            String[] companyAndDivision = getCompanyAndDivisionAndFileName(file.getAbsolutePath());
            EtlDefinition etlDefn = getEtlDefinitionForFile(tmpFile, companyAndDivision[0], companyAndDivision[1]);
            String checksumSHA1 = FileUtilities.checksumSHA1(file);
            List<UploadJobMaster> byChecksum = iUploadJobMaster.findByChecksum(checksumSHA1);

            if(CollectionUtils.isNotEmpty(byChecksum) || etlDefn == null){
                String status = (etlDefn == null ? UploadStatus.NO_DEF_FOUND : UploadStatus.DUPLICATE_FILE).toString();
                if(etlDefn ==null) etlDefn = new EtlDefinition();

                UploadJobMaster uploadJobMaster = new UploadJobMaster().populateUploadJobMaster(etlDefn,file.getName(),configuredCompany,configuredDivision,checksumSHA1);
                uploadJobMaster.setFilename(tmpFile.getPath());
                iUploadJobMaster.save(uploadJobMaster);
                iUploadJobMaster.flush();

                logger.trace("Generating upload id for upload job master entry");
                int id = uploadJobMaster.getId().intValue();
                //  jobMasterEntry.setUploadId(flowName + id);
                uploadJobMaster.setUploadId(etlDefn.getEtlFlow() + id + DateTimeFormat.forPattern("ddMMyyyy").print(new LocalDate()));
                uploadJobMaster.setStatus(status);

                iUploadJobMaster.save(uploadJobMaster);
                iUploadJobMaster.flush();

                etlManager.populateImEventLog(uploadJobMaster);

                handler.moveOrDeleteOriginalFile(file);
                logger.debug("Since The File Is Duplicate returning the call");
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

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

}
