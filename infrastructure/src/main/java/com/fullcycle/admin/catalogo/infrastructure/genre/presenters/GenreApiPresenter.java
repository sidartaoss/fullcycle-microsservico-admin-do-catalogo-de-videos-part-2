package com.fullcycle.admin.catalogo.infrastructure.genre.presenters;

import com.fullcycle.admin.catalogo.application.genre.retrieve.get.GetGenreByIdOutput;
import com.fullcycle.admin.catalogo.application.genre.retrieve.list.GenreListOutput;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GenreListResponse;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GetGenreByIdResponse;

public interface GenreApiPresenter {

    static GetGenreByIdResponse present(final GetGenreByIdOutput output) {
        return new GetGenreByIdResponse(
                output.id(),
                output.name(),
                output.activationStatus(),
                output.categories(),
                output.createdAt(),
                output.updatedAt(),
                output.deletedAt()
        );
    }

    static GenreListResponse present(final GenreListOutput output) {
        return new GenreListResponse(
                output.id(),
                output.name(),
                output.activationStatus(),
                output.createdAt(),
                output.deletedAt()
        );
    }
}
