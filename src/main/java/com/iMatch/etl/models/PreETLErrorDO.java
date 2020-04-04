package com.iMatch.etl.models;

import com.iMatch.etl.enums.Channel;
import com.iMatch.etl.exceptions.UploadError;

import java.math.BigDecimal;

public class PreETLErrorDO implements IFileUploaderDO {

    private static final long serialVersionUID = 1L;
    private String userId;
    private String dataEmailIdIn;
    private Channel channel;
    private UploadError error;
    private String downloadedFile;
    private String archivePath;
    private String genericType;
    private String displayName;
    private String dataEmailIdOut;
    private String zipFilename;
    private String uploadId;
    private boolean companyAgnostic = false;
    private String originalFileName;
    private BigDecimal parentJobId;
    private String company;
    private String division;

    public String getExtEmailId(){
        if(dataEmailIdOut != null)return dataEmailIdOut;
        if(dataEmailIdIn != null)return dataEmailIdIn;
        return null;
    }
    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public boolean isCompanyAgnostic() {
        return companyAgnostic;
    }

    public void setCompanyAgnostic(boolean companyAgnostic) {
        this.companyAgnostic = companyAgnostic;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getZipFilename() {
        return zipFilename;
    }

    public void setZipFilename(String zipFilename) {
        this.zipFilename = zipFilename;
    }

    public String getDataEmailIdOut() {
        return dataEmailIdOut;
    }

    public void setDataEmailIdOut(String dataEmailIdOut) {
        this.dataEmailIdOut = dataEmailIdOut;
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

    public String getArchivePath() {
        return archivePath;
    }

    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
    }

    public UploadError getError() {
        return error;
    }

    public void setError(UploadError error) {
        this.error = error;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getDataEmailIdIn() {
        return dataEmailIdIn;
    }

    public void setDataEmailIdIn(String dataEmailIdIn) {
        this.dataEmailIdIn = dataEmailIdIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getParentJobId() {
        return parentJobId;
    }

    public void setParentJobId(BigDecimal parentJobId) {
        this.parentJobId = parentJobId;
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
