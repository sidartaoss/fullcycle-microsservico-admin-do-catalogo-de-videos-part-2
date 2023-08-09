package com.fullcycle.admin.catalogo.application.genre.update;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UpdateGenreUseCaseTest extends UseCaseTest {

    @Mock
    CategoryGateway categoryGateway;

    @Mock
    GenreGateway genreGateway;

    @InjectMocks
    DefaultUpdateGenreUseCase updateGenreUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway, genreGateway);
    }

    @Nested
    @DisplayName("Update a genre with valid command")
    class UpdateWithValidCommand {

        @Test
        void Given_a_valid_command_When_calls_update_genre_Then_should_return_genre_id() {
            // given
            final var aGenre = Genre.newGenre("acao");
            final var expectedId = aGenre.getId();
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final var expectedName = "Ação";
            final var expectedCategories = List.<CategoryID>of();

            final var aCommand = UpdateGenreCommand.with(expectedId.getValue(),
                    expectedName, asString(expectedCategories));

            when(genreGateway.findById(any()))
                    .thenReturn(Optional.of(Genre.with(aGenre)));

            when(genreGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = updateGenreUseCase.execute(aCommand);

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
                            && Objects.isNull(anUpdatedGenre.getDeletedAt())
            ));
        }

        @Test
        void Given_a_valid_command_with_categories_When_calls_update_genre_Then_should_return_genre_id() {
            // given
            final var aGenre = Genre.newGenre("acao");
            final var expectedId = aGenre.getId();
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final var expectedName = "Ação";
            final var expectedCategories = List.of(
                    CategoryID.from("123"),
                    CategoryID.from("456")
            );

            final var aCommand = UpdateGenreCommand.with(expectedId.getValue(),
                    expectedName, asString(expectedCategories));

            when(genreGateway.findById(any()))
                    .thenReturn(Optional.of(Genre.with(aGenre)));

            doReturn(expectedCategories).when(categoryGateway)
                    .existsByIds(any());

            when(genreGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = updateGenreUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertEquals(expectedId.getValue(), actualOutput.id());

            verify(genreGateway, times(1)).findById(expectedId);

            verify(categoryGateway, times(1)).existsByIds(expectedCategories);

            verify(genreGateway, times(1)).update(argThat(anUpdatedGenre ->
                    Objects.equals(expectedId, anUpdatedGenre.getId())
                            && Objects.equals(expectedName, anUpdatedGenre.getName())
                            && Objects.equals(expectedIsActive, anUpdatedGenre.getActivationStatus())
                            && Objects.equals(expectedCategories, anUpdatedGenre.getCategories())
                            && Objects.equals(aGenre.getCreatedAt(), anUpdatedGenre.getCreatedAt())
                            && anUpdatedGenre.getUpdatedAt().isAfter(aGenre.getUpdatedAt())
                            && Objects.isNull(anUpdatedGenre.getDeletedAt())
            ));
        }
    }

    @Nested
    @DisplayName("Update a genre with invalid command")
    class UpdateWithInvalidCommand {

        @Test
        void Given_an_invalid_name_When_calls_update_genre_Then_should_return_notification_exception() {
            // given
            final var aGenre = Genre.newGenre("acao");
            final var expectedId = aGenre.getId();
            final var expectedCategories = List.<CategoryID>of();

            final var expectedErrorMessage = "'name' should not be empty";
            final var expectedErrorCount = 1;

            final var aCommand = UpdateGenreCommand.with(expectedId.getValue(), " ",
                    asString(expectedCategories));

            when(genreGateway.findById(any()))
                    .thenReturn(Optional.of(Genre.with(aGenre)));

            // when
            Executable invalidMethodCall = () -> updateGenreUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(genreGateway, times(1)).findById(expectedId);

            verify(categoryGateway, never()).existsByIds(any());
            verify(genreGateway, never()).update(any());
        }

        @Test
        void Given_an_invalid_name_When_calls_update_and_some_categories_do_not_exist_Then_should_return_notification() {
            // given
            final var aGenre = Genre.newGenre("acao");
            final var expectedId = aGenre.getId();

            final var filmes = CategoryID.from("456");
            final var series = CategoryID.from("123");
            final var documentarios = CategoryID.from("789");
            final var expectedCategories = List.of(filmes, series, documentarios);

            final var anExpectedErrorMessageOne = "Some categories could not be found: 456, 789";
            final var anExpectedErrorMessageTwo = "'name' should not be null";
            final var expectedErrorCount = 2;

            final var aCommand = UpdateGenreCommand.with(expectedId.getValue(), null,
                    asString(expectedCategories));

            when(genreGateway.findById(any()))
                    .thenReturn(Optional.of(Genre.with(aGenre)));

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(List.of(series));

            // when
            Executable invalidMethodCall = () -> updateGenreUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(anExpectedErrorMessageOne, actualException.getErrors().get(0).message());
            assertEquals(anExpectedErrorMessageTwo, actualException.getErrors().get(1).message());

            verify(genreGateway, times(1)).findById(expectedId);

            verify(categoryGateway, times(1)).existsByIds(expectedCategories);
            verify(genreGateway, never()).update(any());
        }
    }

    private List<String> asString(final List<CategoryID> ids) {
        return ids.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}
