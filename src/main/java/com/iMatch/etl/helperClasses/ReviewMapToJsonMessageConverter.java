package com.iMatch.etl.helperClasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iMatch.etl.internal.ReviewDetails;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anishjoseph on 08/06/17.
 */
@Converter(autoApply = true)
public class ReviewMapToJsonMessageConverter implements AttributeConverter<Map<BigDecimal,List<ReviewDetails>>, String> {
    @Override
    public String convertToDatabaseColumn(Map<BigDecimal,List<ReviewDetails>> value) {
        try {
            String serialized = new ObjectMapper().writeValueAsString(value);
            return serialized;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<BigDecimal,List<ReviewDetails>> convertToEntityAttribute(String value) {
        try {
            Map<BigDecimal,List<ReviewDetails>> values = new ObjectMapper().readValue(value, HashMap.class);
            return values;
        }catch (Exception e){
            return new HashMap<>();
        }
    }
}
