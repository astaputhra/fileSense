package com.iMatch.etl.datauploader.internal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "uploadParam")

public class UploadParam {
	
	private String name;
	private String value;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

}
