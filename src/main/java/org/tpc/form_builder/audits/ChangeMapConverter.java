package org.tpc.form_builder.audits;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.Map;

@Converter
public class ChangeMapConverter implements AttributeConverter<Map<String, ChangeDto>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, ChangeDto> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not convert map to JSON string", e);
        }
    }

    @Override
    public Map<String, ChangeDto> convertToEntityAttribute(String dbData) {
        try {
            JavaType type = objectMapper.getTypeFactory()
                    .constructMapType(Map.class, String.class, ChangeDto.class);
            return objectMapper.readValue(dbData, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not convert JSON string to map", e);
        }
    }
}
