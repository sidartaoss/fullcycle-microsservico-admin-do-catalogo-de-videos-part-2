package com.fullcycle.admin.catalogo.application.genre.retrieve.list;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ListGenresUseCaseTest extends UseCaseTest {

    @Mock
    GenreGateway genreGateway;

    @InjectMocks
    DefaultListGenresUseCase listGenresUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Nested
    @DisplayName("List genres with a valid query")
    class ListWithAValidQuery {

        @Test
        void Given_a_valid_query_When_calls_list_genres_Then_should_return_a_non_empty_list_output() {
            // given
            final var genres = List.of(
                    Genre.newGenre("Ação"),
                    Genre.newGenre("Drama"));
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
            final var expectedPagination = new Pagination<>(
                    expectedPage,
                    expectedPerPage,
                    expectedTotal,
                    genres);

            when(genreGateway.findAll(any()))
                    .thenReturn(expectedPagination);

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

            when(genreGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

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

    @Nested
    @DisplayName("List genres with a valid query and a generic error from gateway")
    class ListWithAValidQueryAndAGenericGatewayError {

        private static final String GATEWAY_ERROR = "Gateway error";

        @Test
        void Given_a_valid_query_When_a_generic_gateway_error_is_thrown_Then_should_return_an_error_message() {
            // given
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            when(genreGateway.findAll(any()))
                    .thenThrow(new IllegalStateException(GATEWAY_ERROR));

            // when
            Executable invalidMethodCall = () -> listGenresUseCase.execute(aQuery);

            // then
            final var actualException = assertThrows(IllegalStateException.class, invalidMethodCall);
            assertNotNull(GATEWAY_ERROR, actualException.getMessage());

            verify(genreGateway, times(1)).findAll(aQuery);
        }
    }
}
