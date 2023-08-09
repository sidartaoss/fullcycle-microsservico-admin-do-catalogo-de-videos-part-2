package com.fullcycle.admin.catalogo.application.genre.create;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IntegrationTest
public class CreateGenreUseCaseIT {

    @Autowired
    private CreateGenreUseCase createGenreUseCase;

    @SpyBean
    private CategoryGateway categoryGateway;

    @SpyBean
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Nested
    @DisplayName("Create a genre with valid command")
    class CreateWithValidCommand {

        @Test
        void Given_a_valid_command_with_categories_When_calls_create_genre_Then_should_return_genre_id() {
            // given
            final var filmes =
                    categoryGateway.create(Category.newCategory("Filmes", " "));

            final var expectedName = "Ação";
            final var expectedCategories = List.of(filmes.getId());
            final var aCommand = CreateGenreCommand
                    .with(expectedName, asString(expectedCategories));
            final var expectedIsActive = ActivationStatus.ACTIVE;

            assertEquals(0, genreRepository.count());

            // when
            final var actualOutput = createGenreUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            assertEquals(1, genreRepository.count());

            genreRepository.findById(actualOutput.id())
                    .ifPresent(actualGenre -> {

                        assertEquals(expectedName, actualGenre.getName());
                        assertEquals(expectedIsActive, actualGenre.getActivationStatus());
                        assertEquals(sorted(expectedCategories), sorted(actualGenre.getCategoryIDs()));
                        assertNotNull(actualGenre.getCreatedAt());
                        assertNotNull(actualGenre.getUpdatedAt());
                        assertNull(actualGenre.getDeletedAt());
                    });
        }

        @Test
        void Given_a_valid_command_without_categories_When_calls_create_genre_Then_should_return_genre_id() {
            // given
            final var expectedName = "Ação";
            final var expectedCategories = List.<CategoryID>of();
            final var aCommand =
                    CreateGenreCommand.with(expectedName,
                            asString(expectedCategories));
            final var expectedIsActive = ActivationStatus.ACTIVE;

            assertEquals(0, genreRepository.count());

            // when
            final var actualOutput = createGenreUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            assertEquals(1, genreRepository.count());

            genreRepository.findById(actualOutput.id())
                    .ifPresent(actualGenre -> {

                        assertEquals(expectedName, actualGenre.getName());
                        assertEquals(expectedIsActive, actualGenre.getActivationStatus());
                        assertEquals(sorted(expectedCategories), sorted(actualGenre.getCategoryIDs()));
                        assertNotNull(actualGenre.getCreatedAt());
                        assertNotNull(actualGenre.getUpdatedAt());
                        assertNull(actualGenre.getDeletedAt());
                    });
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
                    CreateGenreCommand.with(expectedName,
                            asString(expectedCategories));

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
        void Given_an_invalid_name_When_calls_create_and_some_categories_do_not_exist_Then_should_return_exception() {
            // given
            final var series =
                    categoryGateway.create(Category.newCategory("Séries", " "));

            final var expectedName = " ";

            final var filmes = CategoryID.from("456");
            final var documentarios = CategoryID.from("789");
            final var expectedCategories = List.of(filmes, series.getId(), documentarios);

            final var aCommand =
                    CreateGenreCommand.with(expectedName,
                            asString(expectedCategories));

            final var anExpectedErrorMessageOne = "Some categories could not be found: 456, 789";
            final var anExpectedErrorMessageTwo = "'name' should not be empty";
            final var expectedErrorCount = 2;

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

    private List<CategoryID> sorted(final List<CategoryID> expectedCategories) {
        return expectedCategories.stream()
                .sorted(Comparator.comparing(CategoryID::getValue))
                .toList();
    }
}
