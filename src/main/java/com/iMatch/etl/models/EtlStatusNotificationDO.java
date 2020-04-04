package com.iMatch.etl.models;

import com.iMatch.etl.enums.Channel;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class EtlStatusNotificationDO implements IFileUploaderDO, IUploadFileReference {

private static final long serialVersionUID = 1L;

    private String userId;
    private String status;
    private String fileName;
    private String archivePath;
    private String fileType;
    private LocalDate uploadDate;
    private BigDecimal numberOfLinesInput = BigDecimal.ZERO;
	private BigDecimal numberOfErrors = BigDecimal.ZERO;
    private String extEmailId;
    private Channel channel;
    private String dataEmailIdOut;
    private String zipFilename;
    private String uploadId;
    private String uploadGenericType;
    private BigDecimal numberOfLinesOutput;
    private String filename;
    private String etlFlowName;
    private String uploadTempTable;
    private String name;
    private String company;
    private String division;
    private boolean companyAgnostic = false;
    private String uploadedBy;
    private BigDecimal parentJobId;

    private BigDecimal sysTaskId = null;
    private String checksum;
    private String filePath;

    public BigDecimal getSysTaskId() {
        return sysTaskId;
    }

    public void setSysTaskId(BigDecimal sysTaskId) {
        this.sysTaskId = sysTaskId;
    }

    public BigDecimal getParentJobId() {
        return parentJobId;
    }

    public void setParentJobId(BigDecimal parentJobId) {
        this.parentJobId = parentJobId;
    }

    public boolean isCompanyAgnostic() {
        return companyAgnostic;
    }

    public void setCompanyAgnostic(boolean companyAgnostic) {
        this.companyAgnostic = companyAgnostic;
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

    public String getUploadGenericType() {
        return uploadGenericType;
    }

    public void setUploadGenericType(String uploadGenericType) {
        this.uploadGenericType = uploadGenericType;
    }

    public BigDecimal getNumberOfLinesOutput() {
        return numberOfLinesOutput;
    }

    public void setNumberOfLinesOutput(BigDecimal numberOfLinesOutput) {
        this.numberOfLinesOutput = numberOfLinesOutput;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getEtlFlowName() {
        return etlFlowName;
    }

    public void setEtlFlowName(String etlFlowName) {
        this.etlFlowName = etlFlowName;
    }

    public String getUploadTempTable() {
        return uploadTempTable;
    }

    public void setUploadTempTable(String uploadTempTable) {
        this.uploadTempTable = uploadTempTable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    private Map<String,Map<String, String>> errorList = new HashMap<>();

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getArchivePath() {
        return archivePath;
    }

    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public BigDecimal getNumberOfLinesInput() {
        return numberOfLinesInput;
    }

    public void setNumberOfLinesInput(BigDecimal numberOfLinesInput) {
        this.numberOfLinesInput = numberOfLinesInput;
    }

    public BigDecimal getNumberOfErrors() {
        return numberOfErrors;
    }

    public void setNumberOfErrors(BigDecimal numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Map<String, String>> getErrorList() {
        return errorList;
    }

    public void setErrorList(Map<String, Map<String, String>> errorList) {
        this.errorList = errorList;
    }

    public EtlStatusNotificationDO() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExtEmailId() {
        return extEmailId;
    }

    public void setExtEmailId(String extEmailId) {
        this.extEmailId = extEmailId;
    }


    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public EtlStatusNotificationDO(String userId, String status, String fileName, String archivePath, String fileType, LocalDate uploadDate, BigDecimal numberOfLinesInput,BigDecimal numberOfErrors,BigDecimal numberOfLinesOutput, String extEmailId, Map<String, Map<String, String>> errorList, Channel channel, String zipFilename, String uploadId, BigDecimal parentJobId) {
        this.userId = userId;
        this.status = status;
        this.fileName = fileName;
        this.archivePath = archivePath;
        this.fileType = fileType;
        this.uploadDate = uploadDate;
        this.numberOfLinesInput = numberOfLinesInput;
        this.numberOfErrors = numberOfErrors;
        this.extEmailId = extEmailId;
        this.errorList = errorList;
        this.channel = channel;
        this.zipFilename = zipFilename;
        this.uploadId = uploadId;
        this.numberOfLinesOutput = numberOfLinesOutput;
        this.parentJobId = parentJobId;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    @Override
    public String getChecksum() {
        return checksum;
    }
    @Override
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filename) {
        this.filePath = filename;
    }
}
