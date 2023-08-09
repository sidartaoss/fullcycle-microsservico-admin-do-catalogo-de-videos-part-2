package com.fullcycle.admin.catalogo.application.genre.retrieve.list;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IntegrationTest
class ListGenresUseCaseIT {

    @Autowired
    private ListGenresUseCase listGenresUseCase;

    @SpyBean
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Nested
    @DisplayName("List genres with a valid query")
    class ListWithAValidQuery {

        @Test
        void Given_a_valid_query_When_calls_list_genres_Then_should_return_a_non_empty_list_output() {
            // given
            final var genres = List.of(
                    Genre.newGenre("Ação"),
                    Genre.newGenre("Drama"));

            genreRepository.saveAllAndFlush(
                    genres.stream()
                            .map(GenreJpaEntity::from)
                            .toList()
            );

            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "A";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var expectedTotal = 2;
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedItems = genres.stream()
                    .map(GenreListOutput::from)
                    .toList();

            // when
            final var actualOutput = listGenresUseCase.execute(aQuery);

            // then
            assertNotNull(actualOutput);
            assertEquals(expectedPage, actualOutput.currentPage());
            assertEquals(expectedPerPage, actualOutput.perPage());
            assertEquals(expectedTotal, actualOutput.total());
            assertTrue(expectedItems.size() == actualOutput.items().size()
                    && expectedItems.containsAll(actualOutput.items()));
            verify(genreGateway, times(1)).findAll(aQuery);
        }
    }

    @Nested
    @DisplayName("List genres with a valid query and has no result")
    class ListWithAValidQueryAndHasNoResult {

        @Test
        void Given_a_valid_query_When_calls_list_genres_and_has_no_result_Then_should_return_empty_list() {
            // given
            final var genres = List.<Genre>of();

            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var expectedTotal = 0;
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedItems = List.<GenreListOutput>of();

            final var expectedPagination = new Pagination<>(
                    expectedPage,
                    expectedPerPage,
                    expectedTotal,
                    genres);

            // when
            final var actualOutput = listGenresUseCase.execute(aQuery);
            // then
            assertNotNull(actualOutput);
            assertEquals(expectedPage, actualOutput.currentPage());
            assertEquals(expectedPerPage, actualOutput.perPage());
            assertEquals(expectedTotal, actualOutput.total());
            assertEquals(expectedItems, actualOutput.items());

            verify(genreGateway, times(1)).findAll(aQuery);
        }
    }
}
