package com.iMatch.etl.helperClasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iMatch.etl.internal.ReviewDetails;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anishjoseph on 08/06/17.
 */
@Converter(autoApply = true)
public class ReviewListToJsonMessageConverter implements AttributeConverter<List<ReviewDetails>, String> {
    @Override
    public String convertToDatabaseColumn(List<ReviewDetails> value) {
        try {
            String serialized = new ObjectMapper().writeValueAsString(value);
            return serialized;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ReviewDetails> convertToEntityAttribute(String value) {
        try {
            List<ReviewDetails> values = new ObjectMapper().readValue(value, ArrayList.class);
            return values;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
}
