package com.fullcycle.admin.catalogo.application.genre.retrieve.get;

import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.Genre;

import java.time.Instant;
import java.util.List;

public record GetGenreByIdOutput(
        String id,
        String name,
        ActivationStatus activationStatus,
        List<String> categories,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

    public static GetGenreByIdOutput from(final Genre aGenre) {
        final var anId = aGenre.getId().getValue();
        final var aName = aGenre.getName();
        final var anActivationStatus = aGenre.getActivationStatus();
        final var aCategories = aGenre.getCategories().stream()
                .map(CategoryID::getValue)
                .toList();
        final var aCreatedAt = aGenre.getCreatedAt();
        final var anUpdatedAt = aGenre.getUpdatedAt();
        final var aDeletedAt = aGenre.getDeletedAt();
        return new GetGenreByIdOutput(
                anId, aName, anActivationStatus, aCategories, aCreatedAt, anUpdatedAt, aDeletedAt);
    }
}
