package com.zentra.middleware.core.converter;

import com.zentra.middleware.core.enums.Ambiente;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AmbienteConverter implements AttributeConverter<Ambiente, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Ambiente attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCodigo();
    }

    @Override
    public Ambiente convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return Ambiente.fromCodigo(dbData);
    }
}
