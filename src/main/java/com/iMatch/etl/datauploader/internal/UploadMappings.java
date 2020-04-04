package com.iMatch.etl.datauploader.internal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(namespace = "http://www.hexagon.in/UploadMappingGrammer")
public class UploadMappings {

	@XmlElement(name = "mappingDefinition")
	private ArrayList<MappingDefinition> mappingDefinition;

	public void setMapping(ArrayList<MappingDefinition> mappingDefinition) {
		this.mappingDefinition = mappingDefinition;
	}

	public ArrayList<MappingDefinition> getMapping() {
		return this.mappingDefinition;
	}

}
