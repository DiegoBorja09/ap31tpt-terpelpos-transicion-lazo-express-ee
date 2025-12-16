package com.infrastructure.database;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.postgresql.util.PGobject;

@Converter(autoApply = true)
public class JsonPGObjectConverter implements AttributeConverter<String, PGobject> {

    @Override
    public PGobject convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(attribute);
            return jsonObject;
        } catch (Exception e) {
            throw new IllegalArgumentException("Valor JSON inválido para d_atributos", e);
        }
    }

    @Override
    public String convertToEntityAttribute(PGobject dbData) {
        if (dbData == null) {
            return null;
        }
        return dbData.getValue();
    }
}


