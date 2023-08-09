package com.fullcycle.admin.catalogo.application.genre.delete;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeleteGenreUseCaseTest extends UseCaseTest {

    @Mock
    GenreGateway genreGateway;

    @InjectMocks
    DefaultDeleteGenreUseCase deleteGenreUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Nested
    @DisplayName("Delete a genre with valid genre id")
    class DeleteWithValidId {

        private static final String GATEWAY_ERROR = "Gateway Error";

        @Test
        void Given_a_valid_genre_id_When_calls_delete_genre_Then_should_delete_genre() {
            // given
            final var expectedName = "Ação";
            final var aGenre = Genre.newGenre(expectedName);
            final var expectedId = aGenre.getId();

            doNothing().when(genreGateway)
                    .deleteById(any());

            // when
            Executable validMethodCall = () -> deleteGenreUseCase.execute(expectedId.getValue());

            // then
            assertDoesNotThrow(validMethodCall);

            verify(genreGateway, times(1)).deleteById(expectedId);
        }

        @Test
        void Given_a_valid_genre_id_When_calls_delete_and_gateway_throws_some_error_Then_should_return_exception() {
            // given
            final var expectedName = "Ação";
            final var aGenre = Genre.newGenre(expectedName);
            final var expectedId = aGenre.getId();

            doThrow(new IllegalStateException(GATEWAY_ERROR))
                    .when(genreGateway).deleteById(any());

            // when
            Executable invalidMethodCall = () -> deleteGenreUseCase.execute(expectedId.getValue());

            // then
            assertThrows(IllegalStateException.class, invalidMethodCall);

            verify(genreGateway, times(1)).deleteById(expectedId);
        }
    }

    @Nested
    @DisplayName("Delete a genre with an invalid genre id")
    class DeleteWithInvalidId {

        @Test
        void Given_an_invalid_genre_id_When_calls_delete_genre_Then_should_return_Ok() {
            // given
            final var expectedId = GenreID.from("invalid");

            doNothing().when(genreGateway)
                    .deleteById(any());

            // when
            Executable validMethodCall = () -> deleteGenreUseCase.execute(expectedId.getValue());

            // then
            assertDoesNotThrow(validMethodCall);

            verify(genreGateway, times(1)).deleteById(expectedId);
        }
    }
}
