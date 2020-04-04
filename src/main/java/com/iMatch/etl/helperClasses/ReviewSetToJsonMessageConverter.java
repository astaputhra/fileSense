package com.iMatch.etl.helperClasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by anishjoseph on 08/06/17.
 */
@Converter(autoApply = true)
public class ReviewSetToJsonMessageConverter implements AttributeConverter<Set<String>, String> {
    @Override
    public String convertToDatabaseColumn(Set<String> value) {
        try {
            String serialized = new ObjectMapper().writeValueAsString(value);
            return serialized;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> convertToEntityAttribute(String value) {
        try {
            Set<String> values = new ObjectMapper().readValue(value, HashSet.class);
            return values;
        }catch (Exception e){
            return new HashSet<>();
        }
    }
}
