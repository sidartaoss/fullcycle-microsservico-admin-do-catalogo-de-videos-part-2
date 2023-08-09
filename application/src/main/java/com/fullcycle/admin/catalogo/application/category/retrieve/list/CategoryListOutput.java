package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.Category;

import java.time.Instant;

public record CategoryListOutput(
        String id,
        String name,
        String description,
        ActivationStatus activationStatus,
        Instant createdAt,
        Instant deletedAt
) {

    public static CategoryListOutput from(Category aCategory) {
        return new CategoryListOutput(
                aCategory.getId().getValue(),
                aCategory.getName(),
                aCategory.getDescription(),
                aCategory.getActivationStatus(),
                aCategory.getCreatedAt(),
                aCategory.getDeletedAt()
        );
    }
}
