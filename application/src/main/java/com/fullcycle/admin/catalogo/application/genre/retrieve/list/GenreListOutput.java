package com.fullcycle.admin.catalogo.application.genre.retrieve.list;

import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.Genre;

import java.time.Instant;
import java.util.List;

public record GenreListOutput(
        String id,
        String name,
        ActivationStatus activationStatus,
        List<String> categories,
        Instant createdAt,
        Instant deletedAt
) {

    public static GenreListOutput from(final Genre aGenre) {
        final var anId = aGenre.getId().getValue();
        final var aName = aGenre.getName();
        final var anActivationStatus = aGenre.getActivationStatus();
        final var aCategories = aGenre.getCategories()
                .stream()
                .map(CategoryID::getValue)
                .toList();
        final var aCreatedAt = aGenre.getCreatedAt();
        final var aDeletedAt = aGenre.getDeletedAt();
        return new GenreListOutput(anId, aName, anActivationStatus, aCategories, aCreatedAt, aDeletedAt);
    }
}
