package com.fullcycle.admin.catalogo.infrastructure.api.controllers;

import com.fullcycle.admin.catalogo.application.genre.activate.ActivateGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.create.CreateGenreCommand;
import com.fullcycle.admin.catalogo.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.deactivate.DeactivateGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.delete.DeleteGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.fullcycle.admin.catalogo.application.genre.retrieve.list.GenreListOutput;
import com.fullcycle.admin.catalogo.application.genre.retrieve.list.ListGenresUseCase;
import com.fullcycle.admin.catalogo.application.genre.update.UpdateGenreCommand;
import com.fullcycle.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.api.GenreAPI;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GenreListResponse;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GetGenreByIdResponse;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.presenters.GenreApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final UpdateGenreUseCase updateGenreUseCase;
    private final ActivateGenreUseCase activateGenreUseCase;
    private final DeactivateGenreUseCase deactivateGenreUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;
    private final ListGenresUseCase listGenresUseCase;

    public GenreController(
            final CreateGenreUseCase createGenreUseCase,
            final GetGenreByIdUseCase getGenreByIdUseCase,
            final UpdateGenreUseCase updateGenreUseCase,
            final ActivateGenreUseCase activateGenreUseCase,
            final DeactivateGenreUseCase deactivateGenreUseCase,
            final DeleteGenreUseCase deleteGenreUseCase,
            final ListGenresUseCase listGenresUseCase) {
        this.createGenreUseCase = Objects.requireNonNull(createGenreUseCase);
        this.getGenreByIdUseCase = Objects.requireNonNull(getGenreByIdUseCase);
        this.updateGenreUseCase = Objects.requireNonNull(updateGenreUseCase);
        this.activateGenreUseCase = Objects.requireNonNull(activateGenreUseCase);
        this.deactivateGenreUseCase = Objects.requireNonNull(deactivateGenreUseCase);
        this.deleteGenreUseCase = Objects.requireNonNull(deleteGenreUseCase);
        this.listGenresUseCase = Objects.requireNonNull(listGenresUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateGenreRequest aRequest) {
        final var aName = aRequest.name();
        final var aCategories = aRequest.categories();
        final var aCommand = CreateGenreCommand.with(aName, aCategories);
        final var output = this.createGenreUseCase.execute(aCommand);
        return ResponseEntity.created(URI.create("/genres/%s".formatted(output.id())))
                .body(output);
    }

    @Override
    public Pagination<GenreListResponse> list(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String direction) {
        final var aQuery = new SearchQuery(page, perPage, search, sort, direction);
        return listGenresUseCase.execute(aQuery)
                .map(GenreApiPresenter::present);
    }

    @Override
    public GetGenreByIdResponse getById(final String anId) {
        final var output = this.getGenreByIdUseCase.execute(anId);
        return GenreApiPresenter.present(output);
    }

    @Override
    public ResponseEntity<?> update(final String anId,
                                    final UpdateGenreRequest aRequest) {
        final var aName = aRequest.name();
        final var aCategories = aRequest.categories();
        final var aCommand = UpdateGenreCommand.with(anId, aName, aCategories);
        final var output = this.updateGenreUseCase.execute(aCommand);
        return ResponseEntity.ok(output);
    }

    @Override
    public ResponseEntity<?> activate(final String anId) {
        final var output = this.activateGenreUseCase.execute(anId);
        return ResponseEntity.ok(output);
    }

    @Override
    public ResponseEntity<?> deactivate(final String anId) {
        final var output = this.deactivateGenreUseCase.execute(anId);
        return ResponseEntity.ok(output);
    }

    @Override
    public void delete(final String anId) {
        this.deleteGenreUseCase.execute(anId);
    }
}
