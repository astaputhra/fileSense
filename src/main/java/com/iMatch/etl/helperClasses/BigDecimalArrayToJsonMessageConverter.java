package com.iMatch.etl.helperClasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anishjoseph on 08/06/17.
 */
@Converter(autoApply = true)
public class BigDecimalArrayToJsonMessageConverter implements AttributeConverter<List<BigDecimal>, String> {
    @Override
    public String convertToDatabaseColumn(List<BigDecimal> value) {
        try {
            String serialized = new ObjectMapper().writeValueAsString(value);
            return serialized;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BigDecimal> convertToEntityAttribute(String value) {
        try {
            List<BigDecimal> values = new ObjectMapper().readValue(value, ArrayList.class);
            return values;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
}
