package com.iMatch.etl.models;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class DuplicateFileReceivedForUploadDO implements IFileUploaderDO {

    private static final long serialVersionUID = 1L;

    private String uploadId;
    private String source;
    private LocalDate uploadDate;
    private String portfolio;
    private String updFileName;
    private String messageKey;
    List<DuplicateFileErrorDO> duplicateFileErrorList = new ArrayList<>();

    public String getUploadId() {
		return uploadId;
	}
	
	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public LocalDate getUploadDate() {
		return uploadDate;
	}
	
	public void setUploadDate(LocalDate uploadDate) {
		this.uploadDate = uploadDate;
	}

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public String getUpdFileName() {
        return updFileName;
    }

    public void setUpdFileName(String updFileName) {
        this.updFileName = updFileName;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public List<DuplicateFileErrorDO> getDuplicateFileErrorList() {
        return duplicateFileErrorList;
    }

    public void setDuplicateFileErrorList(List<DuplicateFileErrorDO> duplicateFileErrorList) {
        this.duplicateFileErrorList = duplicateFileErrorList;
    }
}
