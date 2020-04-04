package com.iMatch.etl.models;

import com.iMatch.etl.enums.Channel;
import com.iMatch.etl.exceptions.UploadError;

public class FileReceivedForUploadDO implements IFileUploaderDO, IDuplicableDO, IUploadFileReference {

	private static final long serialVersionUID = 1L;

	private String genericType;
	private String displayName;
	private String downloadedFile;
    private String userId;
    private Channel channel;
    private String dataEmailIdIn;
    private UploadError error;
    private String zipFilename;
    private String dataEmailIdOut;
    private String tmpFileName;
    private String originalFileName;
    private String fileChecksum;
    private String processName;
    private String etlFlowName;
    private String company;
    private String division;

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getTmpFileName() {
        return tmpFileName;
    }

    public void setTmpFileName(String tmpFileName) {
        this.tmpFileName = tmpFileName;
    }


    public String getDataEmailIdOut() {
        return dataEmailIdOut;
    }

    public void setDataEmailIdOut(String dataEmailIdOut) {
        this.dataEmailIdOut = dataEmailIdOut;
    }


    public String getZipFilename() {
        return zipFilename;
    }

    public void setZipFilename(String zipFilename) {
        this.zipFilename = zipFilename;
    }


    public UploadError getError() {
        return error;
    }

    public void setError(UploadError error) {
        this.error = error;
    }

    public String getDataEmailIdIn() {
        return dataEmailIdIn;
    }

    public void setDataEmailIdIn(String dataEmailIdIn) {
        this.dataEmailIdIn = dataEmailIdIn;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getGenericType() {
		return genericType;
	}

	public void setGenericType(String genericType) {
		this.genericType = genericType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDownloadedFile() {
		return downloadedFile;
	}

	public void setDownloadedFile(String downloadedFile) {
		this.downloadedFile = downloadedFile;
	}

	public FileReceivedForUploadDO() {
		super();
	}

    @Override
    public String getChecksum() {
        return fileChecksum;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String getFilePath() {
        return downloadedFile;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public FileReceivedForUploadDO(String genericType, String displayName,
			String downloadedFile) {
		super();
		this.genericType = genericType;
		this.displayName = displayName;
		this.downloadedFile = downloadedFile;
	}

    public String getFileChecksum() {
        return fileChecksum;
    }

    public void setFileChecksum(String fileChecksum) {
        this.fileChecksum = fileChecksum;
    }

    public String getEtlFlowName() {
        return etlFlowName;
    }

    public void setEtlFlowName(String etlFlowName) {
        this.etlFlowName = etlFlowName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    @Override
    public Object[] getKey() {
        if (processName != null)  return new Object[]{processName};

        if (fileChecksum == null )   return null;
        return new Object[]{fileChecksum};
    }

    @Override
    public boolean skipError() {
        if (processName != null) return true;
        else return false;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }
}
