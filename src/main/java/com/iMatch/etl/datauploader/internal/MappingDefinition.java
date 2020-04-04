package com.iMatch.etl.datauploader.internal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mappingDefinition")

public class MappingDefinition {

	private String type;
	private String historyTableName;
	private String tableName;
	private String additionalTables;
    private String postUploadClass;
    private String postUploadEvent;
    private String alternateProcess;
    private String alternateUploadEvent;
    private boolean transaction;
    private String roConverter;
    private boolean isAlternateProcess = false;

    public String getRoConverter() {
        return roConverter;
    }

    public void setRoConverter(String roConverter) {
        this.roConverter = roConverter;
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public String getHistoryTableName() {
		return historyTableName;
	}

	public void setHistoryTableName(String historyTableName) {
		this.historyTableName = historyTableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

    public String getPostUploadClass() {
        return postUploadClass;
    }

    public void setPostUploadClass(String postUploadClass) {
        this.postUploadClass = postUploadClass;
    }

    public String getPostUploadEvent() {
        return postUploadEvent;
    }

    public void setPostUploadEvent(String postUploadEvent) {
        this.postUploadEvent = postUploadEvent;
    }

    public boolean isTransaction() {
        if(postUploadClass != null) {
            return false;
        }
        return true;
    }

    public String getAlternateProcess() {
        return alternateProcess;
    }

    public void setAlternateProcess(String alternateProcess) {
        this.alternateProcess = alternateProcess;
    }

    public String getAlternateUploadEvent() {
        return alternateUploadEvent;
    }

    public void setAlternateUploadEvent(String alternateUploadEvent) {
        this.alternateUploadEvent = alternateUploadEvent;
    }

    public boolean getIsAlternateProcess() {
        return isAlternateProcess;
    }

    public void setIsAlternateProcess(boolean isAlternateProcess) {
        this.isAlternateProcess = isAlternateProcess;
    }

    public String getAdditionalTables() {
        return additionalTables;
    }

    public void setAdditionalTables(String additionalTables) {
        this.additionalTables = additionalTables;
    }
}
