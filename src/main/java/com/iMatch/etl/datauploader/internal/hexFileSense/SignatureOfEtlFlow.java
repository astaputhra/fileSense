package com.iMatch.etl.datauploader.internal.hexFileSense;


import java.util.HashMap;
import java.util.Map;

public class SignatureOfEtlFlow {
	private String etlFlowName;
	private String etlType;
	private String desc;
	private String fileNamePattern;
	private String displayName;
    private Map<String, String> senseParams = new HashMap<String, String>();

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEtlFlowName() {
        return etlFlowName;
    }

    public void setEtlFlowName(String etlFlowName) {
        this.etlFlowName = etlFlowName;
    }

    public String getEtlType() {
        return etlType;
    }

    public void setEtlType(String etlType) {
        this.etlType = etlType;
    }

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    public Map<String, String> getSenseParams() {
        return senseParams;
    }

    public void setSenseParams(Map<String, String> senseParams) {
        this.senseParams = senseParams;
    }
}
