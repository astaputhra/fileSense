package com.iMatch.etl.models;

import java.math.BigDecimal;
import java.util.Map;

public class DuplicateFileErrorDO implements IFileUploaderDO{

    public DuplicateFileErrorDO(String rowNo, Map<String, String> stringMapMap, String uploadId) {
        setUpdColumnName(stringMapMap.keySet().toArray()[0].toString());
        setUpdErrorMessage(stringMapMap.values().toArray()[0].toString());
        setUploadId(uploadId);
        setUpdRecordNumber(BigDecimal.valueOf(Long.parseLong(rowNo)));
    }

    public DuplicateFileErrorDO() {

    }

    private String uploadId;
    private BigDecimal updRecordNumber = BigDecimal.ZERO;
    private String updErrorMessage;
    private String updColumnName;

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public BigDecimal getUpdRecordNumber() {
        return updRecordNumber;
    }

    public void setUpdRecordNumber(BigDecimal updRecordNumber) {
        this.updRecordNumber = updRecordNumber;
    }

    public String getUpdErrorMessage() {
        return updErrorMessage;
    }

    public void setUpdErrorMessage(String updErrorMessage) {
        this.updErrorMessage = updErrorMessage;
    }

    public String getUpdColumnName() {
        return updColumnName;
    }

    public void setUpdColumnName(String updColumnName) {
        this.updColumnName = updColumnName;
    }
}
