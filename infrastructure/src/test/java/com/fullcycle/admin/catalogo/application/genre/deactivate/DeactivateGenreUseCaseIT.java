package com.fullcycle.admin.catalogo.application.genre.deactivate;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.application.genre.deactivate.DeactivateGenreUseCase;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IntegrationTest
class DeactivateGenreUseCaseIT {

    @Autowired
    private DeactivateGenreUseCase deactivateGenreUseCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Nested
    @DisplayName("Deactivate a genre with valid command")
    class DeactivateWithValidCommand {

        @Test
        void Given_a_valid_command_with_categories_When_calls_deactivate_genre_Then_should_return_genre_id() {
            // given
            final var filmes =
                    categoryGateway.create(Category.newCategory("Filmes", " "));

            final var expectedName = "Ação";
            final var expectedCategories = List.of(filmes.getId());
            final var aGenre = genreGateway.create(Genre.newGenre(expectedName)
                    .addCategories(expectedCategories));
            final var expectedId = aGenre.getId();
            final var expectedIsActive = ActivationStatus.INACTIVE;

            assertEquals(1, genreRepository.count());

            // when
            final var actualOutput = deactivateGenreUseCase.execute(expectedId.getValue());

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            assertEquals(1, genreRepository.count());

            genreRepository.findById(actualOutput.id())
                    .ifPresent(actualGenre -> {

                        assertEquals(expectedName, actualGenre.getName());
                        assertEquals(expectedIsActive, actualGenre.getActivationStatus());
                        assertTrue(expectedCategories.size() == actualGenre.getCategoryIDs().size()
                                && expectedCategories.containsAll(actualGenre.getCategoryIDs()));
                        assertNotNull(actualGenre.getCreatedAt());
                        assertNotNull(actualGenre.getUpdatedAt());
                        assertNotNull(actualGenre.getDeletedAt());
                    });
        }
    }

    @Nested
    @DisplayName("Deactivate a genre with an invalid id")
    class DeactivateWithInvalidId {

        @Test
        void Given_an_invalid_id_When_calls_deactivate_genre_by_id_Then_should_return_not_found() {
            // given
            final var expectedId = GenreID.from("123");
            final var expectedErrorMessage = "Genre with ID %s was not found"
                    .formatted(expectedId.getValue());

            // when
            Executable invalidMethodCall = () -> deactivateGenreUseCase.execute(expectedId.getValue());

            // then
            final var actualException = assertThrows(NotFoundException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }
    }
}
