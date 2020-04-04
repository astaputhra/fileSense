package com.iMatch.etl.models;

import com.iMatch.etl.enums.Channel;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EtlConsolidatedStatusNotificationDO extends  EtlStatusNotificationDO  {

    private static final long serialVersionUID = 1L;

    private List<BigDecimal> complianceBreachedJobIds = new ArrayList<>();
    private List<BigDecimal> validationApprovableBreachedJobIds = new ArrayList<>();
    private List<UpdError> possibleErrors = new ArrayList<>();

    public EtlConsolidatedStatusNotificationDO(String userId, String status, String fileName, String archivePath, String fileType, LocalDate uploadDate, BigDecimal numberOfLinesInput, BigDecimal numberOfErrors, BigDecimal numberOfLinesOutput, String extEmailId, Map<String, Map<String, String>> errorList, Channel channel, String zipFilename, String uploadId, BigDecimal parentJobId) {
        super(userId, status, fileName, archivePath, fileType, uploadDate, numberOfLinesInput, numberOfErrors, numberOfLinesOutput, extEmailId, errorList, channel, zipFilename, uploadId, parentJobId);
    }


    public List<BigDecimal> getComplianceBreachedJobIds() {
        return complianceBreachedJobIds;
    }

    public void setComplianceBreachedJobIds(List<BigDecimal> complianceBreachedJobId) {
        this.complianceBreachedJobIds = complianceBreachedJobId;
    }

    public List<UpdError> getPossibleErrors() {
        return possibleErrors;
    }

    public void setPossibleErrors(List<UpdError> possibleErrors) {
        this.possibleErrors = possibleErrors;
    }

    public List<BigDecimal> getValidationApprovableBreachedJobIds() {
        return validationApprovableBreachedJobIds;
    }

    public void setValidationApprovableBreachedJobIds(List<BigDecimal> validationApprovableBreachedJobIds) {
        this.validationApprovableBreachedJobIds = validationApprovableBreachedJobIds;
    }
}

