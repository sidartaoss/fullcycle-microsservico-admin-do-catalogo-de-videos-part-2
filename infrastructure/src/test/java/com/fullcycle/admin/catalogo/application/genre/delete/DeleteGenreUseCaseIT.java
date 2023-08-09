package com.fullcycle.admin.catalogo.application.genre.delete;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IntegrationTest
class DeleteGenreUseCaseIT {

    @Autowired
    private DeleteGenreUseCase deleteGenreUseCase;

    @SpyBean
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Nested
    @DisplayName("Delete a genre with valid genre id")
    class DeleteWithValidId {

        private static final String GATEWAY_ERROR = "Gateway Error";

        @Test
        void Given_a_valid_genre_id_When_calls_delete_genre_Then_should_delete_genre() {
            // given
            final var expectedName = "Ação";
            final var aGenre = genreGateway.create(Genre.newGenre(expectedName));
            final var expectedId = aGenre.getId();

            assertEquals(1, genreRepository.count());

            // when
            Executable validMethodCall = () -> deleteGenreUseCase.execute(expectedId.getValue());

            // then
            assertDoesNotThrow(validMethodCall);
            assertEquals(0, genreRepository.count());

            verify(genreGateway, times(1)).deleteById(expectedId);
        }
    }

    @Nested
    @DisplayName("Delete a genre with an invalid genre id")
    class DeleteWithInvalidId {

        @Test
        void Given_an_invalid_genre_id_When_calls_delete_genre_Then_should_return_Ok() {
            // given
            final var expectedName = "Ação";
            genreGateway.create(Genre.newGenre(expectedName));

            final var expectedId = GenreID.from("invalid");

            assertEquals(1, genreRepository.count());

            // when
            Executable validMethodCall = () -> deleteGenreUseCase.execute(expectedId.getValue());

            // then
            assertDoesNotThrow(validMethodCall);
            assertEquals(1, genreRepository.count());

            verify(genreGateway, times(1)).deleteById(expectedId);
        }
    }
}
