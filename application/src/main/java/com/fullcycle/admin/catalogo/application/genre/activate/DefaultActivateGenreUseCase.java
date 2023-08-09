package com.fullcycle.admin.catalogo.application.genre.activate;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultActivateGenreUseCase extends ActivateGenreUseCase {

    private final GenreGateway genreGateway;

    public DefaultActivateGenreUseCase(final GenreGateway genreGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public ActivateGenreOutput execute(final String anId) {
        final var aGenreID = GenreID.from(anId);
        final var aGenre = this.genreGateway.findById(aGenreID)
                .orElseThrow(notFound(aGenreID));

        final var aNotification = Notification.create();
        aNotification.validate(aGenre::activate);

        if (aNotification.hasErrors()) {
            throw new NotificationException("Could not update Aggregate Genre %s"
                    .formatted(anId), aNotification);
        }
        return ActivateGenreOutput.from(
                this.genreGateway.update(aGenre));
    }

    private Supplier<NotFoundException> notFound(final Identifier anId) {
        return () -> NotFoundException.with(Genre.class, anId);
    }
}
