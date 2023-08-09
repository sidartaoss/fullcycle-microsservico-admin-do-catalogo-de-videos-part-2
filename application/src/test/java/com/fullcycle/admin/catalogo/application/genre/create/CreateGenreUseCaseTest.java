package com.fullcycle.admin.catalogo.application.genre.create;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CreateGenreUseCaseTest extends UseCaseTest {

    @Mock
    CategoryGateway categoryGateway;

    @Mock
    GenreGateway genreGateway;

    @InjectMocks
    DefaultCreateGenreUseCase createGenreUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway, genreGateway);
    }

    @Nested
    @DisplayName("Create a genre with valid command")
    class CreateWithValidCommand {

        @Test
        void Given_a_valid_command_When_calls_create_genre_Then_should_return_a_genre_id() {
            // given
            final var expectedName = "Ação";
            final var expectedCategories = List.<CategoryID>of();
            final var aCommand =
                    CreateGenreCommand.with(expectedName, asString(expectedCategories));
            final var expectedIsActive = ActivationStatus.ACTIVE;

            when(genreGateway.create(any(Genre.class)))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = createGenreUseCase.execute(aCommand);
            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());
            verify(genreGateway, times(1)).create(argThat(aGenre ->
                    Objects.nonNull(aGenre.getId())
                            && Objects.equals(expectedName, aGenre.getName())
                            && Objects.equals(expectedIsActive, aGenre.getActivationStatus())
                            && Objects.equals(expectedCategories, aGenre.getCategories())
                            && Objects.nonNull(aGenre.getCreatedAt())
                            && Objects.nonNull(aGenre.getUpdatedAt())
                            && Objects.isNull(aGenre.getDeletedAt())
            ));
        }

        @Test
        void Given_a_valid_command_with_categories_When_calls_create_genre_Then_should_return_genre_id() {
            // given
            final var expectedName = "Ação";
            final var expectedCategories = List.of(
                    CategoryID.from("123"),
                    CategoryID.from("456"));
            final var aCommand =
                    CreateGenreCommand.with(expectedName,
                            asString(expectedCategories));
            final var expectedIsActive = ActivationStatus.ACTIVE;

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(expectedCategories);

            when(genreGateway.create(any(Genre.class)))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = createGenreUseCase.execute(aCommand);
            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(categoryGateway, times(1)).existsByIds(expectedCategories);

            verify(genreGateway, times(1)).create(argThat(aGenre ->
                    Objects.nonNull(aGenre.getId())
                            && Objects.equals(expectedName, aGenre.getName())
                            && Objects.equals(expectedIsActive, aGenre.getActivationStatus())
                            && Objects.equals(expectedCategories, aGenre.getCategories())
                            && Objects.nonNull(aGenre.getCreatedAt())
                            && Objects.nonNull(aGenre.getUpdatedAt())
                            && Objects.isNull(aGenre.getDeletedAt())
            ));
        }
    }

    @Nested
    @DisplayName("Create a genre with invalid command")
    class CreateWithInvalidCommand {

        @Test
        void Given_an_invalid_empty_name_When_calls_create_genre_Then_should_return_a_domain_exception() {
            // given
            final var expectedName = " ";
            final var expectedCategories = List.<CategoryID>of();
            final var aCommand =
                    CreateGenreCommand.with(expectedName, asString(expectedCategories));

            final var expectedErrorMessage = "'name' should not be empty";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> createGenreUseCase.execute(aCommand);

            // then
            final var actualException = Assertions.assertThrows(NotificationException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(categoryGateway, never()).existsByIds(any());
            verify(genreGateway, never()).create(any());
        }

        @Test
        void Given_an_invalid_null_name_When_calls_create_genre_Then_should_return_a_domain_exception() {
            // given
            final var expectedCategories = List.<CategoryID>of();
            final var aCommand =
                    CreateGenreCommand.with(null,
                            asString(expectedCategories));

            final var expectedErrorMessage = "'name' should not be null";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> createGenreUseCase.execute(aCommand);

            // then
            final var actualException = Assertions.assertThrows(NotificationException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(categoryGateway, never()).existsByIds(any());
            verify(genreGateway, never()).create(any());
        }

        @Test
        void Given_a_valid_command_When_calls_create_and_some_categories_do_not_exist_Then_should_return_exception() {
            // given
            final var expectedName = "Ação";

            final var filmes = CategoryID.from("456");
            final var series = CategoryID.from("123");
            final var documentarios = CategoryID.from("789");
            final var expectedCategories = List.of(filmes, series, documentarios);

            final var aCommand =
                    CreateGenreCommand.with(expectedName,
                            asString(expectedCategories));

            final var expectedErrorMessage = "Some categories could not be found: 456, 789";
            final var expectedErrorCount = 1;

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(List.of(series));

            // when
            Executable invalidMethodCall = () -> createGenreUseCase.execute(aCommand);

            // then
            final var actualException = Assertions.assertThrows(NotificationException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(categoryGateway, times(1)).existsByIds(any());
            verify(genreGateway, never()).create(any());
        }

        @Test
        void Given_an_invalid_name_When_calls_create_and_some_categories_do_not_exist_Then_should_return_exception() {
            // given
            final var expectedName = " ";

            final var filmes = CategoryID.from("456");
            final var series = CategoryID.from("123");
            final var documentarios = CategoryID.from("789");
            final var expectedCategories = List.of(filmes, series, documentarios);

            final var aCommand =
                    CreateGenreCommand.with(expectedName,
                            asString(expectedCategories));

            final var anExpectedErrorMessageOne = "Some categories could not be found: 456, 789";
            final var anExpectedErrorMessageTwo = "'name' should not be empty";
            final var expectedErrorCount = 2;

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(List.of(series));

            // when
            Executable invalidMethodCall = () -> createGenreUseCase.execute(aCommand);

            // then
            final var actualException = Assertions.assertThrows(NotificationException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(anExpectedErrorMessageOne, actualException.getErrors().get(0).message());
            assertEquals(anExpectedErrorMessageTwo, actualException.getErrors().get(1).message());

            verify(categoryGateway, times(1)).existsByIds(any());
            verify(genreGateway, never()).create(any());
        }
    }

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
                .map(CategoryID::getValue)
                .toList();
    }
}