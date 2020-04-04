package com.iMatch.etl.models;

import org.joda.time.LocalDate;

public class ReviewNotificationForUploadDO implements IFileUploaderDO {

    private static final long serialVersionUID = 1L;

    private String uploadId;
    private String source;
    private LocalDate uploadDate;
    private String portfolio;
    private String updFileName;
    private String moduleRef;
    private String taskCreatedBy;
    private boolean isMoreInfo;

    public ReviewNotificationForUploadDO(){}
    public ReviewNotificationForUploadDO(String uploadId, String source, LocalDate uploadDate,String updFileName, String moduleRef, String taskCreatedBy, boolean isMoreInfo){
        this.uploadId = uploadId;
        this.source   = source;
        this.uploadDate = uploadDate;
        this.updFileName = updFileName;
        this.moduleRef = moduleRef;
        this.taskCreatedBy = taskCreatedBy;
        this.isMoreInfo = isMoreInfo;
    }
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getModuleRef() { return moduleRef; }

    public void setModuleRef(String moduleRef) {this.moduleRef = moduleRef;}

    public boolean getIsMoreInfo() {
        return isMoreInfo;
    }

    public void setIsMoreInfo(boolean isMoreInfo) {
        this.isMoreInfo = isMoreInfo;
    }

    public String getTaskCreatedBy() {
        return taskCreatedBy;
    }

    public void setTaskCreatedBy(String taskCreatedBy) {
        this.taskCreatedBy = taskCreatedBy;
    }
}

