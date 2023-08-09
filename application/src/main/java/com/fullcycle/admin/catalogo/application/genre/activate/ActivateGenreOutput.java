package com.fullcycle.admin.catalogo.application.genre.activate;

import com.fullcycle.admin.catalogo.domain.genre.Genre;

public record ActivateGenreOutput(
        String id
) {

    public static ActivateGenreOutput from(final String anId) {
        return new ActivateGenreOutput(anId);
    }

    public static ActivateGenreOutput from(final Genre aGenre) {
        return new ActivateGenreOutput(aGenre.getId().getValue());
    }
}
