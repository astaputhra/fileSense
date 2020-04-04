package com.iMatch.etl.datauploader.internal;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "uploadConfig")

public class UploadConfig {
	
	private String type;
	private String name;
	private String description;
	private String flowName;
	
	@XmlElement(name = "uploadParam")
	private ArrayList<UploadParam> uploadParams = new ArrayList<UploadParam>();
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getFlowName() {
		return flowName;
	}
	
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public UploadParam[] getUploadParams() {
		return uploadParams.toArray(new UploadParam[uploadParams.size()]);
	}
	
}
