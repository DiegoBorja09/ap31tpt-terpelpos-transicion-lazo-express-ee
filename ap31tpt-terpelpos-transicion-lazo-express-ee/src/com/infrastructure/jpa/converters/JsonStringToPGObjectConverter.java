package com.infrastructure.jpa.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.postgresql.util.PGobject;

/**
 * Converts a JSON string into a PostgreSQL json typed value so the JDBC driver
 * binds it with the correct database type instead of VARCHAR.
 */
@Converter(autoApply = false)
public class JsonStringToPGObjectConverter implements AttributeConverter<String, Object> {

    @Override
    public Object convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(attribute);
            return jsonObject;
        } catch (Exception ex) {
            // Fallback to raw string if PGobject fails for some reason
            return attribute;
        }
    }

    @Override
    public String convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }
        return dbData.toString();
    }
}



