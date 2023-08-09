package com.fullcycle.admin.catalogo.application.genre.retrieve.get;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GetGenreByIdUseCaseTest extends UseCaseTest {

    @Mock
    GenreGateway genreGateway;

    @InjectMocks
    DefaultGetGenreByIdUseCase getGenreByIdUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Nested
    @DisplayName("Get a genre by id with a valid id")
    class GetGenreByIdWithValidId {

        @Test
        void Given_a_valid_id_When_calls_get_genre_by_id_Then_should_return_an_ouput_category() {
            // given
            final var expectedName = "Ação";
            final var expectedCategories = List.of(
                    CategoryID.from("123"),
                    CategoryID.from("456"));
            final var aGenre = Genre.newGenre(expectedName)
                    .addCategories(expectedCategories);
            final var expectedId = aGenre.getId();
            final var expectedIsActive = aGenre.getActivationStatus();
            final var expectedCreatedAt = aGenre.getCreatedAt();
            final var expectedUpdatedAt = aGenre.getUpdatedAt();
            final var expectedDeletedAt = aGenre.getDeletedAt();

            when(genreGateway.findById(any()))
                    .thenReturn(Optional.of(Genre.with(aGenre)));
            // when
            final var actualOutput = getGenreByIdUseCase.execute(expectedId.getValue());
            // then
            assertNotNull(actualOutput);
            assertEquals(expectedId.getValue(), actualOutput.id());
            assertEquals(expectedName, actualOutput.name());
            assertEquals(expectedIsActive, actualOutput.activationStatus());
            assertEquals(asString(expectedCategories), actualOutput.categories());
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

            when(genreGateway.findById(expectedId))
                    .thenReturn(Optional.empty());

            // when
            Executable invalidMethodCall = () -> getGenreByIdUseCase.execute(expectedId.getValue());

            // then
            final var actualException = assertThrows(NotFoundException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }
    }

    private List<String> asString(final List<CategoryID> ids) {
        return ids.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}
