package com.iMatch.etl.datauploader.internal;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingConfig  implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MappingConfig.class);

	Resource typeMapping;

    private Map<String, MappingDefinition> mappingDefinitions;

    public void setTypeMapping(Resource typeMapping) {
		this.typeMapping = typeMapping;
	}

    public void init(){run();}

    @Override
    public void run()
	{
        mappingDefinitions = new HashMap<String, MappingDefinition>();
        UploadMappings config = new UploadMappings();
        logger.trace("Parsing xml file {}", typeMapping.getFilename());
        try {
            JAXBContext context = JAXBContext.newInstance(UploadMappings.class);
            Unmarshaller um = context.createUnmarshaller();
            try {

                config = (UploadMappings) um.unmarshal(typeMapping.getInputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (JAXBException e) {
            // TODO error handling
            throw new RuntimeException(e);
        }

        verifyAndGenerateMappingDefinitions(config);
	}

    private void verifyAndGenerateMappingDefinitions(UploadMappings uploadMappings) {
        if(uploadMappings.getMapping() == null) {
            logger.trace("No mapping definitions found for upload");
            return;
        }
        for(MappingDefinition definition : uploadMappings.getMapping()) {
            logger.trace("Verifying mapping type : {}", definition.getType());

            if(!definition.isTransaction()) {

                logger.trace("{} is non transactional, verifying sub classes", definition.getType());

                logger.trace("Looking for class {}", definition.getPostUploadClass());
                try {
                    Class.forName(definition.getPostUploadClass());
                } catch (ClassNotFoundException e) {
                    logger.trace("{} not found", definition.getPostUploadClass());
                    //               throw (new HexGenInternalError);
                    //FIXME should make this an HexgenInternalError
                    throw new RuntimeException("Post processing class " + definition.getPostUploadClass() + " not found");
                }

                if(definition.getPostUploadEvent() != null) {
                    logger.trace("Looking for class {}", definition.getPostUploadEvent());
                    try {
                        Class.forName(definition.getPostUploadEvent());
                    } catch (ClassNotFoundException e) {
                        logger.trace("{} not found", definition.getPostUploadEvent());
                        //               throw (new HexGenInternalError);
                        //FIXME should make this an HexgenInternalError
                        throw new RuntimeException("Post processing class " + definition.getPostUploadEvent() + " not found");
                    }
                }
            }

//            mappingDefinitions.put(definition.getType(), definition);
        }
    }

    public List<MappingDefinition> getMappingTypes() {
        return (List<MappingDefinition>) mappingDefinitions.values();
    }

    public MappingDefinition getMappingForType(String genericType) {
        return mappingDefinitions.get(genericType);
    }

}