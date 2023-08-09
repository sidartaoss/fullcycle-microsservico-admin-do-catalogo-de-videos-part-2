package com.fullcycle.admin.catalogo.infrastructure.genre;

import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class GenreMySQLGateway implements GenreGateway {

    private final GenreRepository genreRepository;

    public GenreMySQLGateway(final GenreRepository genreRepository) {
        this.genreRepository = Objects.requireNonNull(genreRepository);
    }

    @Override
    public Genre create(final Genre aGenre) {
        return this.save(aGenre);
    }

    @Override
    public void deleteById(final GenreID anId) {
        final var anIdValue = anId.getValue();
        final var exists = this.genreRepository.existsById(anIdValue);
        if (exists) {
            this.genreRepository.deleteById(anIdValue);
        }
    }

    @Override
    public Optional<Genre> findById(GenreID anId) {
        return this.genreRepository.findById(anId.getValue())
                .map(GenreJpaEntity::toAggregate);
    }

    @Override
    public Genre update(final Genre aGenre) {
        return this.save(aGenre);
    }

    @Override
    public Pagination<Genre> findAll(final SearchQuery aQuery) {
        final var page = aQuery.page();
        final var perPage = aQuery.perPage();
        final var sort = Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort());
        final var pageRequest = PageRequest.of(page, perPage, sort);
        final var terms = aQuery.terms();
        final var specifications = isTermsNotInfomed(terms) ? filterOneEqualsOne() : filterLikeName(terms);
        final Page<GenreJpaEntity> pageResult = this.genreRepository.findAll(Specification
                .where(specifications), pageRequest);
        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult
                        .map(GenreJpaEntity::toAggregate)
                        .toList()
        );
    }

    private Genre save(final Genre aGenre) {
        return this.genreRepository.save(GenreJpaEntity.from(aGenre))
                .toAggregate();
    }

    private Specification<GenreJpaEntity> filterLikeName(String terms) {
        return SpecificationUtils.<GenreJpaEntity>like("name", terms);
    }

    private Specification<GenreJpaEntity> filterOneEqualsOne() {
        return SpecificationUtils.oneEqualsOne();
    }

    private boolean isTermsNotInfomed(final String terms) {
        return terms == null || terms.isBlank();
    }
}
