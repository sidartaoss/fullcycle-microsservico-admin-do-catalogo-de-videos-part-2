package com.fullcycle.admin.catalogo.application.genre.deactivate;

import com.fullcycle.admin.catalogo.domain.genre.Genre;

public record DeactivateGenreOutput(
        String id
) {

    public static DeactivateGenreOutput from(final String anId) {
        return new DeactivateGenreOutput(anId);
    }

    public static DeactivateGenreOutput from(final Genre aGenre) {
        return new DeactivateGenreOutput(aGenre.getId().getValue());
    }
}
