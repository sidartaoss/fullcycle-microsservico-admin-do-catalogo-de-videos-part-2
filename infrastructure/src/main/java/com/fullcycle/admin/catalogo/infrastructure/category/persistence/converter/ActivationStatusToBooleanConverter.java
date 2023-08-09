package com.fullcycle.admin.catalogo.infrastructure.category.persistence.converter;

import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ActivationStatusToBooleanConverter implements AttributeConverter<ActivationStatus, Boolean> {


    @Override
    public Boolean convertToDatabaseColumn(ActivationStatus attribute) {
        return ActivationStatus.ACTIVE.equals(attribute) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public ActivationStatus convertToEntityAttribute(Boolean dbData) {
        return Boolean.TRUE.equals(dbData) ? ActivationStatus.ACTIVE : ActivationStatus.INACTIVE;
    }
}
