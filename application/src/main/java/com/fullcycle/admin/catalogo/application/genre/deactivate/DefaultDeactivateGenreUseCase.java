package com.fullcycle.admin.catalogo.application.genre.deactivate;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultDeactivateGenreUseCase extends DeactivateGenreUseCase {

    private final GenreGateway genreGateway;

    public DefaultDeactivateGenreUseCase(final GenreGateway genreGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public DeactivateGenreOutput execute(final String anId) {
        final Genre aGenre = getGenre(anId);

        final var aNotification = Notification.create();
        aNotification.validate(aGenre::deactivate);

        if (aNotification.hasErrors()) {
            throw new NotificationException("Could not update Aggregate Genre %s"
                    .formatted(anId), aNotification);
        }
        return DeactivateGenreOutput.from(
                this.genreGateway.update(aGenre));
    }

    private Genre getGenre(final String anId) {
        final var aGenreID = GenreID.from(anId);
        return this.genreGateway.findById(aGenreID)
                .orElseThrow(notFound(aGenreID));
    }

    private Supplier<NotFoundException> notFound(final Identifier anId) {
        return () -> NotFoundException.with(Genre.class, anId);
    }
}
