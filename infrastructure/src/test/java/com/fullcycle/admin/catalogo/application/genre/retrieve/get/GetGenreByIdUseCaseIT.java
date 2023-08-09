package com.fullcycle.admin.catalogo.application.genre.retrieve.get;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IntegrationTest
class GetGenreByIdUseCaseIT {

    @Autowired
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @SpyBean
    private GenreGateway genreGateway;

    @Nested
    @DisplayName("Get a genre by id with a valid id")
    class GetGenreByIdWithValidId {

        @Test
        void Given_a_valid_id_When_calls_get_genre_by_id_Then_should_return_an_ouput_category() {
            // given
            final var filmes =
                    categoryGateway.create(Category.newCategory("Filmes", " "));
            final var series =
                    categoryGateway.create(Category.newCategory("Séries", " "));

            final var expectedName = "Ação";
            final var expectedCategories = List.of(filmes.getId(), series.getId());
            final var aGenre = genreGateway.create(Genre.newGenre(expectedName)
                    .addCategories(expectedCategories));
            final var expectedId = aGenre.getId();
            final var expectedIsActive = aGenre.getActivationStatus();
            final var expectedCreatedAt = aGenre.getCreatedAt();
            final var expectedUpdatedAt = aGenre.getUpdatedAt();
            final var expectedDeletedAt = aGenre.getDeletedAt();

            // when
            final var actualOutput = getGenreByIdUseCase.execute(expectedId.getValue());
            // then
            assertNotNull(actualOutput);
            assertEquals(expectedId.getValue(), actualOutput.id());
            assertEquals(expectedName, actualOutput.name());
            assertEquals(expectedIsActive, actualOutput.activationStatus());
            assertTrue(
                    asString(expectedCategories).size() == actualOutput.categories().size()
                    && asString(expectedCategories).containsAll(actualOutput.categories()));
            assertEquals(expectedCreatedAt, actualOutput.createdAt());
            assertEquals(expectedUpdatedAt, actualOutput.updatedAt());
            assertEquals(expectedDeletedAt, actualOutput.deletedAt());

            verify(genreGateway, times(1)).findById(expectedId);
        }
    }

    @Nested
    @DisplayName("Get a genre by id with an invalid id")
    class GetGenreByIdWithInvalidId {

        @Test
        void Given_an_invalid_id_When_calls_get_genre_by_id_Then_should_return_not_found() {
            // given
            final var expectedId = GenreID.from("123");
            final var expectedErrorMessage = "Genre with ID %s was not found"
                    .formatted(expectedId.getValue());

            // when
            Executable invalidMethodCall = () -> getGenreByIdUseCase.execute(expectedId.getValue());

            // then
            final var actualException = assertThrows(NotFoundException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }
    }

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}
