package com.fullcycle.admin.catalogo.application.genre.retrieve.get;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetGenreByIdUseCase extends GetGenreByIdUseCase {

    private final GenreGateway genreGateway;

    public DefaultGetGenreByIdUseCase(final GenreGateway genreGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public GetGenreByIdOutput execute(final String anId) {
        final var aGenreID = GenreID.from(anId);
        return this.genreGateway.findById(aGenreID)
                .map(GetGenreByIdOutput::from)
                .orElseThrow(notFound(aGenreID));
    }

    private Supplier<NotFoundException> notFound(final GenreID anId) {
        return () -> NotFoundException.with(Genre.class, anId);
    }
}
