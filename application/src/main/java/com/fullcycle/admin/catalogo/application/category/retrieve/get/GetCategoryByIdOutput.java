package com.fullcycle.admin.catalogo.application.category.retrieve.get;

import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.Category;

import java.time.Instant;

public record GetCategoryByIdOutput(
        String id,
        String name,
        String description,
        ActivationStatus activationStatus,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

    public static GetCategoryByIdOutput from(final Category aCategory) {
        final var anId = aCategory.getId().getValue();
        final var aName = aCategory.getName();
        final var aDescription = aCategory.getDescription();
        final var anActivationStatus = aCategory.getActivationStatus();
        final var aCreatedAt = aCategory.getCreatedAt();
        final var anUpdatedAt = aCategory.getUpdatedAt();
        final var aDeletedAt = aCategory.getDeletedAt();
        return new GetCategoryByIdOutput(
                anId, aName, aDescription, anActivationStatus, aCreatedAt, anUpdatedAt, aDeletedAt);
    }
}
