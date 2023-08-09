package com.fullcycle.admin.catalogo.domain.genre;

import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GenreTest {

    @DisplayName("Create a new genre with valid params")
    @Nested
    class CreateWithValidParams {

        @Test
        void Given_valid_params_When_calls_newGenre_Then_should_instantiate_a_genre() {
            // given
            final var expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;
            final var expectedCategories = 0;
            final var expectedNow = Instant.now().truncatedTo(ChronoUnit.SECONDS);

            // when
            final var actualGenre = Genre.newGenre(expectedName);

            // then
            assertNotNull(actualGenre);
            assertNotNull(actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertEquals(expectedCategories, actualGenre.getCategories().size());
            assertEquals(expectedNow, actualGenre.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
            assertEquals(expectedNow, actualGenre.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
            assertNull(actualGenre.getDeletedAt());
        }
    }

    @DisplayName("Create a new genre with invalid params")
    @Nested
    class CreateWithInvalidParams {

        @Test
        void Given_invalid_null_name_When_calls_newGenre_and_validate_Then_should_receive_an_error() {
            // given
            final var expectedErrorMessage = "'name' should not be null";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> Genre.newGenre(null);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }

        @Test
        void Given_invalid_empty_name_When_calls_newGenre_and_validate_Then_should_receive_an_error() {
            // given
            final String expectedName = " ";
            final var expectedErrorMessage = "'name' should not be empty";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> Genre.newGenre(expectedName);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }

        @Test
        void Given_name_with_length_greater_than_255_When_calls_newGenre_and_validate_Then_should_receive_an_error() {
            // given
            final String expectedName = """
                    Todas estas questões, devidamente ponderadas, levantam dúvidas sobre se a estrutura atual da organização acarreta um processo de reformulação e modernização do orçamento setorial. A certificação de metodologias que nos auxiliam a lidar com a consulta aos diversos militantes facilita a criação de todos os recursos funcionais envolvidos.
                    """;
            final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> Genre.newGenre(expectedName);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }
    }

    @DisplayName("Deactivate an active genre")
    @Nested
    class Deactivate {

        @Test
        void Given_an_active_genre_When_calls_deactivate_Then_should_receive_ok() {
            // given
            final var expectedName = "Ação";
            final var expectedCategories = 0;

            final var aGenre = Genre.newGenre(expectedName);
            assertNotNull(aGenre);
            assertEquals(ActivationStatus.ACTIVE, aGenre.getActivationStatus());
            assertNull(aGenre.getDeletedAt());
            final var updatedAt = aGenre.getUpdatedAt();

            // when
            final var actualGenre = aGenre.deactivate();

            // then
            assertNotNull(actualGenre);
            assertEquals(ActivationStatus.INACTIVE, actualGenre.getActivationStatus());
            assertNotNull(actualGenre.getDeletedAt());

            assertNotNull(actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedCategories, actualGenre.getCategories().size());
            assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
        }
    }

    @DisplayName("Activate an inactive genre")
    @Nested
    class Activate {

        @Test
        void Given_an_inactive_genre_When_calls_activate_Then_should_receive_ok() {
            // given
            final var expectedName = "Ação";
            final var expectedCategories = 0;

            final var aGenre = Genre.newGenre(expectedName)
                    .deactivate();
            assertNotNull(aGenre);
            assertEquals(ActivationStatus.INACTIVE, aGenre.getActivationStatus());
            assertNotNull(aGenre.getDeletedAt());
            final var updatedAt = aGenre.getUpdatedAt();

            // when
            final var actualGenre = aGenre.activate();

            // then
            assertNotNull(actualGenre);
            assertEquals(ActivationStatus.ACTIVE, actualGenre.getActivationStatus());
            assertNull(actualGenre.getDeletedAt());

            assertNotNull(actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedCategories, actualGenre.getCategories().size());
            assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
        }
    }

    @DisplayName("Update a genre with valid params")
    @Nested
    class UpdateWithValidParams {

        @Test
        void Given_a_valid_inactive_genre_When_calls_update_and_activate_Then_should_receive_genre_updated() {
            // given
            final var expectedName = "Ação";
            final var expectedCategories = List.of(CategoryID.from("234"));

            final var aGenre = Genre.newGenre("acao")
                    .deactivate();
            assertNotNull(aGenre);
            assertEquals(ActivationStatus.INACTIVE, aGenre.getActivationStatus());
            assertNotNull(aGenre.getDeletedAt());
            final var updatedAt = aGenre.getUpdatedAt();

            // when
            final var actualGenre = aGenre.update(expectedName, expectedCategories)
                    .activate();

            // then
            assertNotNull(actualGenre);
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(ActivationStatus.ACTIVE, actualGenre.getActivationStatus());
            assertNull(actualGenre.getDeletedAt());

            assertNotNull(actualGenre.getId());
            assertEquals(expectedCategories, actualGenre.getCategories());
            assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
        }

        @Test
        void Given_a_valid_inactive_genre_When_calls_update_and_inactivate_Then_should_receive_genre_updated() {
            // given
            final var expectedName = "Ação";
            final var expectedCategories = List.of(CategoryID.from("234"));

            final var aGenre = Genre.newGenre("acao");
            assertNotNull(aGenre);
            assertEquals(ActivationStatus.ACTIVE, aGenre.getActivationStatus());
            assertNull(aGenre.getDeletedAt());
            final var updatedAt = aGenre.getUpdatedAt();

            // when
            final var actualGenre = aGenre.update(expectedName, expectedCategories)
                    .deactivate();

            // then
            assertNotNull(actualGenre);
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(ActivationStatus.INACTIVE, actualGenre.getActivationStatus());
            assertNotNull(actualGenre.getDeletedAt());

            assertNotNull(actualGenre.getId());
            assertEquals(expectedCategories, actualGenre.getCategories());
            assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
        }

        @Test
        void Given_valid_genre_When_calls_update_with_empty_categories_Then_should_receive_Ok() {
            // given
            final String expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;
            final List<CategoryID> expectedCategories = new ArrayList<>();

            final var aGenre = Genre.newGenre("acao");
            assertNotNull(aGenre);
            assertNotNull(aGenre.getCategories());

            final var updatedAt = aGenre.getUpdatedAt();

            // when
            final ThrowingSupplier<Genre> validMethodCall = () -> aGenre.update(expectedName, expectedCategories);

            // then
            final var actualGenre = assertDoesNotThrow(validMethodCall);
            assertNotNull(actualGenre);
            assertNotNull(actualGenre.getCategories());
            assertTrue(actualGenre.getCategories().isEmpty());

            assertNotNull(actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
            assertNull(actualGenre.getDeletedAt());
        }

        @Test
        void Given_a_valid_genre_with_two_categories_When_calls_remove_category_Then_should_receive_Ok() {
            // given
            final String expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final var seriesID = CategoryID.from("123");
            final var moviesID = CategoryID.from("234");
            final List<CategoryID> expectedCategories = List.of(moviesID);

            final var aGenre = Genre.newGenre("acao");
            assertNotNull(aGenre);
            assertNotNull(aGenre.getCategories());
            assertTrue(aGenre.getCategories().isEmpty());

            final var updatedAt = aGenre.getUpdatedAt();

            aGenre.update(expectedName, List.of(seriesID, moviesID));
            assertEquals(2, aGenre.getCategories().size());

            // when
            final var actualGenre = aGenre.removeCategory(seriesID);

            // then
            assertFalse(actualGenre.getCategories().isEmpty());
            assertEquals(expectedCategories, actualGenre.getCategories());

            assertNotNull(actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
            assertNull(actualGenre.getDeletedAt());
        }
    }

    @DisplayName("Add category with valid params")
    @Nested
    class AddCategoryWithValidParams {

        @Test
        void Given_a_valid_genre_with_no_categories_When_calls_add_category_Then_should_receive_Ok() {
            // given
            final String expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final var seriesID = CategoryID.from("123");
            final var moviesID = CategoryID.from("234");
            final List<CategoryID> expectedCategories = List.of(seriesID, moviesID);

            final var aGenre = Genre.newGenre(expectedName);
            assertNotNull(aGenre);
            assertNotNull(aGenre.getCategories());
            assertTrue(aGenre.getCategories().isEmpty());

            final var createdAt = aGenre.getCreatedAt();
            final var updatedAt = aGenre.getUpdatedAt();

            // when
            aGenre.addCategory(seriesID);
            aGenre.addCategory(moviesID);

            // then
            assertNotNull(aGenre.getCategories());
            assertFalse(aGenre.getCategories().isEmpty());
            assertEquals(expectedCategories, aGenre.getCategories());

            assertNotNull(aGenre.getId());
            assertEquals(expectedName, aGenre.getName());
            assertEquals(expectedIsActive, aGenre.getActivationStatus());
            assertEquals(createdAt, aGenre.getCreatedAt());
            assertTrue(aGenre.getUpdatedAt().isAfter(updatedAt));
            assertNull(aGenre.getDeletedAt());
        }
    }

    @DisplayName("Add categories with valid params")
    @Nested
    class AddCategoriesWithValidParams {

        @Test
        void Given_a_valid_genre_with_no_categories_When_calls_add_categories_Then_should_receive_Ok() {
            // given
            final String expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final var seriesID = CategoryID.from("123");
            final var moviesID = CategoryID.from("234");
            final List<CategoryID> expectedCategories = List.of(seriesID, moviesID);

            final var aGenre = Genre.newGenre(expectedName);
            assertNotNull(aGenre);
            assertNotNull(aGenre.getCategories());
            assertTrue(aGenre.getCategories().isEmpty());

            final var createdAt = aGenre.getCreatedAt();
            final var updatedAt = aGenre.getUpdatedAt();

            // when
            aGenre.addCategories(expectedCategories);

            // then
            assertNotNull(aGenre.getCategories());
            assertFalse(aGenre.getCategories().isEmpty());
            assertEquals(expectedCategories, aGenre.getCategories());

            assertNotNull(aGenre.getId());
            assertEquals(expectedName, aGenre.getName());
            assertEquals(expectedIsActive, aGenre.getActivationStatus());
            assertEquals(createdAt, aGenre.getCreatedAt());
            assertTrue(aGenre.getUpdatedAt().isAfter(updatedAt));
            assertNull(aGenre.getDeletedAt());
        }

        @Test
        void Given_a_valid_genre_with_no_categories_When_calls_add_categories_with_empty_list_Then_should_receive_Ok() {
            // given
            final String expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final List<CategoryID> expectedCategories = List.of();

            final var aGenre = Genre.newGenre(expectedName);
            assertNotNull(aGenre);
            assertNotNull(aGenre.getCategories());
            assertTrue(aGenre.getCategories().isEmpty());

            final var createdAt = aGenre.getCreatedAt();
            final var updatedAt = aGenre.getUpdatedAt();

            // when
            aGenre.addCategories(expectedCategories);

            // then
            assertNotNull(aGenre.getCategories());
            assertTrue(aGenre.getCategories().isEmpty());
            assertEquals(expectedCategories, aGenre.getCategories());

            assertNotNull(aGenre.getId());
            assertEquals(expectedName, aGenre.getName());
            assertEquals(expectedIsActive, aGenre.getActivationStatus());
            assertEquals(createdAt, aGenre.getCreatedAt());
            assertEquals(updatedAt, aGenre.getUpdatedAt());
            assertNull(aGenre.getDeletedAt());
        }
    }

    @DisplayName("Add category with invalid params")
    @Nested
    class AddCategoryWithInvalidParams {

        @Test
        void Given_an_invalid_null_as_category_id_When_calls_add_category_Then_should_receive_Ok() {
            // given
            final String expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final List<CategoryID> expectedCategories = new ArrayList<>();

            final var aGenre = Genre.newGenre(expectedName);
            assertNotNull(aGenre);
            assertNotNull(aGenre.getCategories());
            assertTrue(aGenre.getCategories().isEmpty());

            final var updatedAt = aGenre.getUpdatedAt();

            // when
            final var actualGenre = aGenre.addCategory(null);

            // then
            assertNotNull(actualGenre.getCategories());
            assertTrue(actualGenre.getCategories().isEmpty());
            assertEquals(expectedCategories, actualGenre.getCategories());

            assertNotNull(actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertEquals(updatedAt, actualGenre.getUpdatedAt());
            assertNull(actualGenre.getDeletedAt());
        }
    }

    @DisplayName("Remove category with valid params")
    @Nested
    class RemoveCategoryWithValidParams {

        @Test
        void Given_an_invalid_null_as_category_id_When_calls_remove_category_Then_should_receive_Ok() {
            // given
            final String expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final var seriesID = CategoryID.from("123");
            final var moviesID = CategoryID.from("234");
            final List<CategoryID> expectedCategories = List.of(moviesID);

            final var aGenre = Genre.newGenre("acao");
            assertNotNull(aGenre);
            assertNotNull(aGenre.getCategories());
            assertTrue(aGenre.getCategories().isEmpty());

            final var updatedAt = aGenre.getUpdatedAt();

            aGenre.update(expectedName, List.of(seriesID, moviesID));
            assertEquals(2, aGenre.getCategories().size());

            // when
            final var actualGenre = aGenre.removeCategory(seriesID);

            // then
            assertFalse(actualGenre.getCategories().isEmpty());
            assertEquals(expectedCategories, actualGenre.getCategories());

            assertNotNull(actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
            assertNull(actualGenre.getDeletedAt());
        }
    }

    @DisplayName("Remove category with invalid params")
    @Nested
    class RemoveCategoryWithInvalidParams {

        @Test
        void Given_an_invalid_null_as_category_id_When_calls_remove_category_Then_should_receive_Ok() {
            // given
            final String expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final var seriesID = CategoryID.from("123");
            final var moviesID = CategoryID.from("234");
            final List<CategoryID> expectedCategories = List.of(seriesID, moviesID);

            final var aGenre = Genre.newGenre("acao");
            assertNotNull(aGenre);
            assertNotNull(aGenre.getCategories());
            assertTrue(aGenre.getCategories().isEmpty());

            final var updatedAt = aGenre.getUpdatedAt();

            aGenre.update(expectedName, expectedCategories);

            // when
            final var actualGenre = aGenre.removeCategory(null);

            // then
            assertNotNull(actualGenre.getCategories());
            assertFalse(actualGenre.getCategories().isEmpty());
            assertEquals(expectedCategories, actualGenre.getCategories());

            assertNotNull(actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
            assertNull(actualGenre.getDeletedAt());
        }
    }

    @DisplayName("Update a genre with invalid params")
    @Nested
    class UpdateWithInvalidParams {

        @Test
        void Given_invalid_null_name_When_calls_update_and_validate_Then_should_receive_an_error() {
            // given
            final var expectedCategories = List.of(CategoryID.from("234"));

            final var aGenre = Genre.newGenre("acao");

            final var expectedErrorMessage = "'name' should not be null";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> aGenre.update(null,
                    expectedCategories);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }

        @Test
        void Given_invalid_empty_name_When_calls_update_and_validate_Then_should_receive_an_error() {
            // given
            final String expectedName = " ";
            final var expectedCategories = List.of(CategoryID.from("234"));

            final var aGenre = Genre.newGenre("acao");

            final var expectedErrorMessage = "'name' should not be empty";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> aGenre.update(expectedName, expectedCategories);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }
    }
}
