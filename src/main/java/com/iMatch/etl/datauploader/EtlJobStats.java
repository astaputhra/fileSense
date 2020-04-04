package com.iMatch.etl.datauploader;

import java.math.BigDecimal;

public class EtlJobStats {

    private String uploadId;

    private BigDecimal numberOfErrors = BigDecimal.ZERO;

    private BigDecimal numberOfLinesInput = BigDecimal.ZERO;

    private BigDecimal numberOfRejected = BigDecimal.ZERO;

    private BigDecimal numberOfLinesOutput = BigDecimal.ZERO;

    private String filename;

    private String etlFlowName;

    private String uploadTempTable;

    public String getEtlFlowName() {
        return etlFlowName;
    }

    public void setEtlFlowName(String etlFlowName) {
        this.etlFlowName = etlFlowName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public BigDecimal getNumberOfErrors() {
        return numberOfErrors;
    }

    public void setNumberOfErrors(BigDecimal numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public BigDecimal getNumberOfLinesInput() {
        return numberOfLinesInput;
    }

    public void setNumberOfLinesInput(BigDecimal numberOfLinesInput) {
        this.numberOfLinesInput = numberOfLinesInput;
    }

    public BigDecimal getNumberOfLinesOutput() {
        return numberOfLinesOutput;
    }

    public void setNumberOfLinesOutput(BigDecimal numberOfLinesOutput) {
        this.numberOfLinesOutput = numberOfLinesOutput;
    }

    public BigDecimal getNumberOfRejected() {
        return numberOfRejected;
    }

    public void setNumberOfRejected(BigDecimal numberOfRejected) {
        this.numberOfRejected = numberOfRejected;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getUploadTempTable() {
        return uploadTempTable;
    }

    public void setUploadTempTable(String uploadTempTable) {
        this.uploadTempTable = uploadTempTable;
    }

}

