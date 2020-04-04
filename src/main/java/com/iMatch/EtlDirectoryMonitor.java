package com.iMatch;

import com.iMatch.etl.datauploader.ETLServiceProvider;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.file.WatchServiceDirectoryScanner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EtlDirectoryMonitor extends AbstractEtlMonitor {
	public static final String TIME_FORMAT = "HH:mm:ss";
	private String root;
	private boolean isCompanyAndDivisionPartOfPath = false;
	@Value("#{appProp['etl.directory.monitor.company']}")
	private String configuredCompany;
	@Value("#{appProp['etl.directory.monitor.division']}")
	private String configuredDivision;

//	@Resource(name = "executorService")
//	private ExecutorService executorService;

	@Value("#{appProp['etl.directory.processed']}")
	private String processedDir;
	private File processedDirFile;

	@Resource(name = "etlService")
	private ETLServiceProvider etlService;
	private WatchServiceDirectoryScanner leafScanner;
	@Value("#{appProp['etl.directory.scannerResetThreshold']}")
	int scannerResetThreshold = 0;

//	@Resource(name="operationChannel")
//	private DirectChannel controlBusChannel;
//
	@PostConstruct
	private void postInit(){
		if (processedDir == null || processedDir.isEmpty()) return;
		if (this.processedDir.endsWith(fileSeparator)) {
			this.processedDir = processedDir.substring(0, processedDir.length() - 1);
		}
		try {
			processedDirFile = new File(processedDir);
			if (!processedDirFile.exists()) {
				boolean success = processedDirFile.mkdir();
				if (!success) processedDirFile = null;
			}
		} catch (Exception e) {
			logger.error("error creating processed directory {}", processedDir,e);
			processedDirFile = null;
		}

	}

	public void setCompanyAndDivisionPartOfPath(boolean companyAndDivisionPartOfPath) {
		isCompanyAndDivisionPartOfPath = companyAndDivisionPartOfPath;
	}

	private static final Logger logger = LoggerFactory.getLogger(EtlDirectoryMonitor.class);

	public String getRoot() {
		return root;
	}

	public void setRoot(String base) {
		root = base;
		if (root.endsWith(fileSeparator)) {
			root = base.substring(0, base.length() - 1);
		}
	}

	public void handler(File recvdFile) {
//		if(!isClusterMaster()){
//			logger.info("File sensed but we have lost cluster mastership - not processing file {}",recvdFile.getName());
//			return;
//		}
		logger.debug("Got Database lock - file is {}", recvdFile.getName());
		logger.trace("received file {} for processing, size {}, last-modified {}", recvdFile.getName(),recvdFile.length(),new Date(recvdFile.lastModified()));
		if(!recvdFile.exists()){
			logger.trace("File {} does not exist", recvdFile.getName());
			//If the file no longer exists - just return
			return;
		}
		if (isFileBlacklisted(recvdFile.getName())) {
			moveOrDeleteOriginalFile(recvdFile);
			return;
		}

        recvdFile.setWritable(true);

		File tmpFile;
		try {
			tmpFile = getTempFile(recvdFile);
		} catch (Exception e) {
			logger.error("error creating temp file for {}, {}", recvdFile.getName(), e.getMessage());
			moveOrDeleteOriginalFile(recvdFile);
//			throw new HexGenInternalError(HexGenMessages.getInternalErrorMessage(InternalErrorMessages.INTERNAL_EXCEPTION_ENCOUNTERED, e.getClass().getName(), e.getMessage()));
			throw new RuntimeException(e);
		}

		if (FilenameUtils.getExtension(tmpFile.getAbsolutePath()).equalsIgnoreCase("zip")) {
			InputStream stream = getStream(tmpFile);
			List<UnzippedFile> files = unzip(stream, tmpFile.getName());
			IOUtils.closeQuietly(stream);
			if (!files.isEmpty()) {
				moveOrDeleteOriginalFile(recvdFile);
				return;
			}
		}
		moveOrDeleteOriginalFile(recvdFile);
	}

	private void moveOrDeleteOriginalFile(File recvdFile) {
		logger.trace("Back-up original file {}, last-modified {}, size {}",recvdFile.getName(), recvdFile.length(), recvdFile.length());
		try {
			if (processedDirFile == null)
				recvdFile.delete();
			else {
				File destDir = new File(processedDirFile.getPath() + fileSeparator + recvdFile.getParent().substring(root.length()));
				if (!destDir.exists()) destDir.mkdir();
				try {
					FileUtils.moveFileToDirectory(recvdFile, destDir,true);
				} catch (FileExistsException e) {
					FileUtils.moveFile(recvdFile, new File(destDir.getPath()+fileSeparator+recvdFile.getName()+"-"+System.currentTimeMillis()));
				}
			}
		} catch (Exception e) {
			logger.error("Error moving original file {} - error is {}",recvdFile.getName(), e.getMessage());
			try {
				boolean deleted = recvdFile.delete();
				if (!deleted)
					logger.debug("Could not delete original file {}",recvdFile.getName());
			} catch (Exception e1) {
				logger.error("Error deleting original file {} - error is {}",recvdFile.getName(), e1.getMessage());
			}
		}
	}

	public String formatDate(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}


	private InputStream getStream(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			logger.error("Error while creating input stream - error is {}", e.getMessage());
			return null;
		} catch (IOException e) {
			logger.error("Error while creating input stream - error is {}", e.getMessage());
		}
		return null;
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


//	@Override
//	public void lostClusterMaster() {
//		Message<String> operation = MessageBuilder.withPayload("@'filesIn.adapter'.stop()").build();
//		controlBusChannel.send(operation);
//		logger.info("Going to STOP the Directory polling");
//	}

//	@Override
//	public void gainedClusterMaster() {
//		Message<String> operation = MessageBuilder.withPayload("@'filesIn.adapter'.start()").build();
//		controlBusChannel.send(operation);
//		logger.info("Going to START the Directory polling");
//	}

	public void setLeafScanner(WatchServiceDirectoryScanner leafScanner) {
		this.leafScanner = leafScanner;
	}

	public WatchServiceDirectoryScanner getLeafScanner() {
		return leafScanner;
	}

	void resetScanner() {
		logger.debug("restarting directory watch service");
		leafScanner.stop();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			//nothing
		}
		leafScanner.start();
		logger.info("directory watch service restarted");
	}

	boolean resetScannerEnabled() {
		return scannerResetThreshold >= 60;
	}
}
