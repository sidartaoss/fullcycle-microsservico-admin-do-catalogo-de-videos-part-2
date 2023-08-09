package com.fullcycle.admin.catalogo.application.genre.deactivate;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeactivateGenreUseCaseTest extends UseCaseTest {

    @Mock
    GenreGateway genreGateway;

    @InjectMocks
    DefaultDeactivateGenreUseCase deactivateGenreUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Nested
    @DisplayName("Deactivate a genre with valid genre id")
    class DeactivateWithValidId {

        @Test
        void Given_a_valid_command_When_calls_deactivate_genre_Then_should_return_genre_id() {
            // given
            final var expectedName = "Ação";
            final var aGenre = Genre.newGenre(expectedName);

            final var expectedId = aGenre.getId();
            final var expectedIsActive = ActivationStatus.INACTIVE;
            final var expectedCategories = List.<CategoryID>of();

            when(genreGateway.findById(any()))
                    .thenReturn(Optional.of(Genre.with(aGenre)));

            when(genreGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = deactivateGenreUseCase.execute(expectedId.getValue());

            // then
            assertNotNull(actualOutput);
            assertEquals(expectedId.getValue(), actualOutput.id());

            verify(genreGateway, times(1)).findById(expectedId);

            verify(genreGateway, times(1)).update(argThat(anUpdatedGenre ->
                    Objects.equals(expectedId, anUpdatedGenre.getId())
                            && Objects.equals(expectedName, anUpdatedGenre.getName())
                            && Objects.equals(expectedIsActive, anUpdatedGenre.getActivationStatus())
                            && Objects.equals(expectedCategories, anUpdatedGenre.getCategories())
                            && Objects.equals(aGenre.getCreatedAt(), anUpdatedGenre.getCreatedAt())
                            && anUpdatedGenre.getUpdatedAt().isAfter(aGenre.getUpdatedAt())
                            && Objects.nonNull(anUpdatedGenre.getDeletedAt())
            ));
        }
    }
}
