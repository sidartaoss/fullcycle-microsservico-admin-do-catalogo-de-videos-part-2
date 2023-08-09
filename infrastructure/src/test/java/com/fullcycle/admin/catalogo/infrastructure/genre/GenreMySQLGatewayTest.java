package com.fullcycle.admin.catalogo.infrastructure.genre;

import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.category.CategoryMySQLGateway;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@MySQLGatewayTest
class GenreMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryMySQLGateway;

    @Autowired
    private GenreMySQLGateway genreMySQLGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Nested
    @DisplayName("Create with a valid genre")
    class CreateWithValidGenre {

        @Test
        void Given_a_valid_genre_with_categories_When_calls_create_genre_Then_should_persist_genre() {
            // Given
            final var filmes = categoryMySQLGateway.create(
                    Category.newCategory("Filmes", " "));

            final var expectedName = "Ação";
            final var expectedCategories = List.of(filmes.getId());

            final var aGenre = Genre.newGenre(expectedName)
                    .addCategories(expectedCategories);

            final var expectedId = aGenre.getId();
            final var expectedIsActive = aGenre.getActivationStatus();

            assertEquals(0, genreRepository.count());

            // When
            final var actualGenre = genreMySQLGateway.create(aGenre);

            // Then
            assertEquals(1, genreRepository.count());

            assertEquals(expectedId, actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertEquals(expectedCategories, actualGenre.getCategories());
            assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
            assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
            assertNull(actualGenre.getDeletedAt());

            genreRepository.findById(expectedId.getValue())
                    .ifPresent(persistedGenre -> {

                        assertEquals(expectedName, persistedGenre.getName());
                        assertEquals(expectedIsActive, persistedGenre.getActivationStatus());
                        assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
                        assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
                        assertEquals(aGenre.getUpdatedAt(), persistedGenre.getUpdatedAt());
                        assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
                        assertNull(persistedGenre.getDeletedAt());
                    });
        }

        @Test
        void Given_a_valid_genre_without_categories_When_calls_create_genre_Then_should_persist_genre() {
            // Given
            final var expectedName = "Ação";
            final var aGenre = Genre.newGenre(expectedName);

            final var expectedId = aGenre.getId();
            final var expectedIsActive = aGenre.getActivationStatus();
            final var expectedCategories = aGenre.getCategories();

            assertEquals(0, genreRepository.count());

            // When
            final var actualGenre = genreMySQLGateway.create(aGenre);

            // Then
            assertEquals(1, genreRepository.count());

            assertEquals(expectedId, actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertEquals(expectedCategories, actualGenre.getCategories());
            assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
            assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
            assertNull(actualGenre.getDeletedAt());

            genreRepository.findById(expectedId.getValue())
                    .ifPresent(persistedGenre -> {

                        assertEquals(expectedName, persistedGenre.getName());
                        assertEquals(expectedIsActive, persistedGenre.getActivationStatus());
                        assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
                        assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
                        assertEquals(aGenre.getUpdatedAt(), persistedGenre.getUpdatedAt());
                        assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
                        assertNull(persistedGenre.getDeletedAt());
                    });
        }
    }

    @Nested
    @DisplayName("Update with a valid genre")
    class UpdateWithValidGenre {

        @Test
        void Given_a_valid_genre_without_categories_When_calls_update_with_categories_Then_should_persist_genre() {
            final var filmes = categoryMySQLGateway.create(Category.newCategory("Filmes", " "));
            final var series = categoryMySQLGateway.create(Category.newCategory("Séries", " "));

            // Given
            final var expectedName = "Ação";
            final var aGenre = Genre.newGenre("acao");
            final var expectedId = aGenre.getId();
            final var expectedIsActive = aGenre.getActivationStatus();
            final var expectedCategories = List.of(filmes.getId(), series.getId());

            assertEquals(0, genreRepository.count());

            genreRepository.save(GenreJpaEntity.from(aGenre));

            assertEquals(1, genreRepository.count());

            genreRepository.findById(expectedId.getValue())
                    .ifPresent(actualInvalidEntity -> {

                        assertEquals("acao", actualInvalidEntity.getName());
                        assertEquals(0, actualInvalidEntity.getCategories().size());
                    });

            final var anUpdatedGenre = Genre.with(aGenre)
                    .update(expectedName, expectedCategories);

            // When
            final var actualGenre = genreMySQLGateway.update(anUpdatedGenre);

            // Then
            assertEquals(1, genreRepository.count());

            assertEquals(expectedId, actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertTrue(expectedCategories.containsAll(actualGenre.getCategories()));
            assertEquals(anUpdatedGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(aGenre.getUpdatedAt()));
            assertEquals(anUpdatedGenre.getDeletedAt(), actualGenre.getDeletedAt());
            assertNull(actualGenre.getDeletedAt());

            genreRepository.findById(expectedId.getValue())
                    .ifPresent(actualEntity -> {

                        assertEquals(expectedName, actualEntity.getName());
                        assertEquals(expectedIsActive, actualEntity.getActivationStatus());
                        assertTrue(expectedCategories.containsAll(actualEntity.getCategoryIDs()));
                        assertEquals(actualGenre.getCreatedAt(), actualEntity.getCreatedAt());
                        assertEquals(actualGenre.getUpdatedAt(), actualEntity.getUpdatedAt());
                        assertEquals(actualGenre.getDeletedAt(), actualEntity.getDeletedAt());
                        assertNull(actualEntity.getDeletedAt());
                    });
        }

        @Test
        void Given_a_valid_genre_with_categories_When_calls_update_cleaning_categories_Then_should_persist_genre() {
            final var filmes = categoryMySQLGateway.create(Category.newCategory("Filmes", " "));
            final var series = categoryMySQLGateway.create(Category.newCategory("Séries", " "));

            // Given
            final var expectedName = "Ação";
            final var aGenre = Genre.newGenre("acao")
                    .addCategories(List.of(filmes.getId(), series.getId()));
            final var expectedId = aGenre.getId();
            final var expectedIsActive = aGenre.getActivationStatus();
            final var expectedCategories = List.<CategoryID>of();

            assertEquals(0, genreRepository.count());

            genreRepository.save(GenreJpaEntity.from(aGenre));

            assertEquals(1, genreRepository.count());

            genreRepository.findById(expectedId.getValue())
                    .ifPresent(actualInvalidEntity -> {

                        assertEquals("acao", actualInvalidEntity.getName());
                        assertEquals(2, actualInvalidEntity.getCategories().size());
                    });

            final var anUpdatedGenre = Genre.with(aGenre)
                    .update(expectedName, expectedCategories);

            // When
            final var actualGenre = genreMySQLGateway.update(anUpdatedGenre);

            // Then
            assertEquals(1, genreRepository.count());

            assertEquals(expectedId, actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertEquals(expectedCategories, actualGenre.getCategories());
            assertEquals(anUpdatedGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(aGenre.getUpdatedAt()));
            assertEquals(anUpdatedGenre.getDeletedAt(), actualGenre.getDeletedAt());
            assertNull(actualGenre.getDeletedAt());

            genreRepository.findById(expectedId.getValue())
                    .ifPresent(actualEntity -> {

                        assertEquals(expectedName, actualEntity.getName());
                        assertEquals(expectedIsActive, actualEntity.getActivationStatus());
                        assertEquals(expectedCategories, actualEntity.getCategoryIDs());
                        assertEquals(actualGenre.getCreatedAt(), actualEntity.getCreatedAt());
                        assertEquals(actualGenre.getUpdatedAt(), actualEntity.getUpdatedAt());
                        assertEquals(actualGenre.getDeletedAt(), actualEntity.getDeletedAt());
                        assertNull(actualEntity.getDeletedAt());
                    });
        }
    }

    @Nested
    @DisplayName("Activate with a valid genre")
    class ActivateWithValidGenre {

        @Test
        void Given_a_valid_genre_When_calls_activate_Then_should_return_an_updated_genre() {
            final var filmes = categoryMySQLGateway.create(Category.newCategory("Filmes", " "));
            final var series = categoryMySQLGateway.create(Category.newCategory("Séries", " "));

            // Given
            final var expectedName = "Ação";
            final var aGenre = Genre.newGenre(expectedName);
            final var expectedId = aGenre.getId();
            final var expectedIsActive = aGenre.getActivationStatus();
            final var expectedCategories = List.of(filmes.getId(), series.getId());

            assertEquals(0, genreRepository.count());
            genreRepository.save(GenreJpaEntity.from(aGenre));
            assertEquals(1, genreRepository.count());

            genreRepository.save(GenreJpaEntity.from(aGenre
                    .deactivate()));
            assertEquals(1, genreRepository.count());

            genreRepository.findById(expectedId.getValue())
                    .ifPresent(actualInvalidEntity -> {

                        assertEquals(expectedName, actualInvalidEntity.getName());
                        assertEquals(0, actualInvalidEntity.getCategories().size());
                        assertEquals(ActivationStatus.INACTIVE, actualInvalidEntity.getActivationStatus());
                    });

            final var anUpdatedGenre = Genre.with(aGenre)
                    .addCategories(expectedCategories)
                    .activate();

            // When
            final var actualGenre = genreMySQLGateway.update(anUpdatedGenre);

            // Then
            assertEquals(1, genreRepository.count());

            assertEquals(expectedId, actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertTrue(expectedCategories.containsAll(actualGenre.getCategories()));
            assertEquals(anUpdatedGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(aGenre.getUpdatedAt()));
            assertEquals(anUpdatedGenre.getDeletedAt(), actualGenre.getDeletedAt());
            assertNull(actualGenre.getDeletedAt());

            genreRepository.findById(expectedId.getValue())
                    .ifPresent(actualEntity -> {

                        assertEquals(expectedName, actualEntity.getName());
                        assertEquals(expectedIsActive, actualEntity.getActivationStatus());
                        assertTrue(expectedCategories.containsAll(actualEntity.getCategoryIDs()));
                        assertEquals(actualGenre.getCreatedAt(), actualEntity.getCreatedAt());
                        assertEquals(actualGenre.getUpdatedAt(), actualEntity.getUpdatedAt());
                        assertEquals(actualGenre.getDeletedAt(), actualEntity.getDeletedAt());
                        assertNull(actualEntity.getDeletedAt());
                    });
        }
    }

    @Nested
    @DisplayName("Deactivate with a valid genre")
    class DeactivateWithValidGenre {

        @Test
        void Given_a_valid_genre_When_calls_deactivate_Then_should_return_an_updated_genre() {
            final var filmes = categoryMySQLGateway.create(Category.newCategory("Filmes", " "));
            final var series = categoryMySQLGateway.create(Category.newCategory("Séries", " "));

            // Given
            final var expectedName = "Ação";
            final var aGenre = Genre.newGenre(expectedName);
            final var expectedId = aGenre.getId();
            final var expectedIsActive = ActivationStatus.INACTIVE;
            final var expectedCategories = List.of(filmes.getId(), series.getId());

            assertEquals(0, genreRepository.count());
            genreRepository.save(GenreJpaEntity.from(aGenre));
            assertEquals(1, genreRepository.count());

            genreRepository.findById(expectedId.getValue())
                    .ifPresent(actualInvalidEntity -> {

                        assertEquals(expectedName, actualInvalidEntity.getName());
                        assertEquals(0, actualInvalidEntity.getCategories().size());
                        assertEquals(ActivationStatus.ACTIVE, actualInvalidEntity.getActivationStatus());
                    });

            final var anUpdatedGenre = Genre.with(aGenre)
                    .addCategories(expectedCategories)
                    .deactivate();

            // When
            final var actualGenre = genreMySQLGateway.update(anUpdatedGenre);

            // Then
            assertEquals(1, genreRepository.count());

            assertEquals(expectedId, actualGenre.getId());
            assertEquals(expectedName, actualGenre.getName());
            assertEquals(expectedIsActive, actualGenre.getActivationStatus());
            assertTrue(expectedCategories.containsAll(actualGenre.getCategories()));
            assertEquals(anUpdatedGenre.getCreatedAt(), actualGenre.getCreatedAt());
            assertTrue(actualGenre.getUpdatedAt().isAfter(aGenre.getUpdatedAt()));
            assertEquals(anUpdatedGenre.getDeletedAt(), actualGenre.getDeletedAt());
            assertNotNull(actualGenre.getDeletedAt());

            genreRepository.findById(expectedId.getValue())
                    .ifPresent(actualEntity -> {

                        assertEquals(expectedName, actualEntity.getName());
                        assertEquals(expectedIsActive, actualEntity.getActivationStatus());
                        assertTrue(expectedCategories.containsAll(actualEntity.getCategoryIDs()));
                        assertEquals(actualGenre.getCreatedAt(), actualEntity.getCreatedAt());
                        assertEquals(actualGenre.getUpdatedAt(), actualEntity.getUpdatedAt());
                        assertEquals(actualGenre.getDeletedAt(), actualEntity.getDeletedAt());
                        assertNotNull(actualEntity.getDeletedAt());
                    });
        }
    }

    @Nested
    @DisplayName("Delete with a valid genre id")
    class DeleteWithValidGenreId {

        @Test
        void Given_a_valid_genre_id_When_calls_delete_Then_should_delete_genre() {
            // Given
            final var expectedName = "Ação";
            final var aGenre = Genre.newGenre(expectedName);
            final var expectedId = aGenre.getId();

            assertEquals(0, genreRepository.count());

            genreRepository.save(GenreJpaEntity.from(aGenre));

            assertEquals(1, genreRepository.count());

            // When
            genreMySQLGateway.deleteById(expectedId);

            // Then
            assertEquals(0, genreRepository.count());
        }
    }

    @Nested
    @DisplayName("Delete with an invalid genre id")
    class DeleteWithInvalidGenreId {

        @Test
        void Given_an_invalid_genre_id_When_calls_delete_Then_should_return_ok() {
            // Given
            final var expectedId = GenreID.from("invalid");

            assertEquals(0, genreRepository.count());

            // When
            genreMySQLGateway.deleteById(expectedId);

            // Then
            assertEquals(0, genreRepository.count());
        }
    }

    @Nested
    @DisplayName("Get genre by a valid id")
    class GetGenreByValidId {

        @Test
        void Given_a_valid_genre_id_When_calls_find_by_id_Then_should_return_genre() {
            // Given
            final var filmes = categoryMySQLGateway.create(Category.newCategory("Filmes", " "));
            final var series = categoryMySQLGateway.create(Category.newCategory("Séries", " "));

            final var expectedName = "Ação";
            final var aGenre = Genre.newGenre(expectedName);
            final var expectedId = aGenre.getId();
            final var expectedIsActive = ActivationStatus.ACTIVE;
            final var expectedCategories = List.of(filmes.getId(), series.getId());

            assertEquals(0, genreRepository.count());

            genreRepository
                    .save(GenreJpaEntity.from(aGenre
                            .addCategories(expectedCategories)));

            assertEquals(1, genreRepository.count());

            // When & Then
            genreMySQLGateway.findById(expectedId)
                    .ifPresent(actualGenre -> {

                        assertEquals(expectedName, actualGenre.getName());
                        assertEquals(expectedIsActive, actualGenre.getActivationStatus());
                        assertEquals(sorted(expectedCategories), sorted(actualGenre.getCategories()));
                        assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
                        assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
                        assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
                        assertNull(actualGenre.getDeletedAt());
                    });
            assertEquals(1, genreRepository.count());
        }

        @Test
        void Given_a_non_stored_valid_genre_id_When_calls_find_by_id_Then_should_return_empty() {
            // Given
            final var nonStoredValidGenreId = GenreID.from("empty");

            assertEquals(0, genreRepository.count());

            // When
            final var actualCategory = genreMySQLGateway.findById(nonStoredValidGenreId);

            // Then
            assertEquals(0, genreRepository.count());
            assertTrue(actualCategory.isEmpty());
        }
    }

    @Nested
    @DisplayName("List paginated genres")
    class ListPaginatedGenres {

        @Test
        void Given_empty_genres_table_When_calls_findAll_Then_should_return_empty_page() {
            // Given
            final var expectedPage = 0;
            final var expectedPerPage = 1;
            final var expectedTotal = 0;
            final var terms = "";
            final var sort = "name";
            final var direction = "asc";

            final var aQuery = new SearchQuery(
                    expectedPage,
                    expectedPerPage,
                    terms,
                    sort,
                    direction
            );

            // When
            final var actualResult = genreMySQLGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertTrue(actualResult.items().isEmpty());

            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedTotal, actualResult.items().size());
            assertEquals(expectedPerPage, actualResult.perPage());
        }

        @ParameterizedTest
        @CsvSource({
            "aç,0,10,1,1,Ação",
            "dr,0,10,1,1,Drama",
            "com,0,10,1,1,Comédia romântica",
            "cien,0,10,1,1,Ficção científica",
            "terr,0,10,1,1,Terror",
        })
        void Given_valid_terms_When_calls_findAll_Then_should_return_filtered(
                final String expectedTerms,
                final int expectedPage,
                final int expectedPerPage,
                final int expectedItemsCount,
                final long expectedTotal,
                final String expectedGenreName
        ) {
            // Given
            mockGenres();
            final var sort = "name";
            final var direction = "asc";

            final var aQuery = new SearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    sort,
                    direction
            );

            // When
            final var actualResult = genreMySQLGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedItemsCount, actualResult.items().size());
            assertEquals(expectedGenreName, actualResult.items().get(0).getName());
        }

        @ParameterizedTest
        @CsvSource({
                "name,asc,0,10,5,5,Ação",
                "name,desc,0,10,5,5,Terror",
                "createdAt,asc,0,10,5,5,Comédia romântica",
                "createdAt,desc,0,10,5,5,Ficção científica",
        })
        void Given_valid_sort_and_direction_When_calls_findAll_Then_should_return_filtered(
                final String expectedSort,
                final String expectedDirection,
                final int expectedPage,
                final int expectedPerPage,
                final int expectedItemsCount,
                final long expectedTotal,
                final String expectedGenreName
        ) {
            // Given
            mockGenres();
            final var expectedTerms = "";

            final var aQuery = new SearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    expectedSort,
                    expectedDirection
            );

            // When
            final var actualResult = genreMySQLGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedItemsCount, actualResult.items().size());
            assertEquals(expectedGenreName, actualResult.items().get(0).getName());
        }

        @ParameterizedTest
        @CsvSource({
                "0,2,2,5,Ação;Comédia romântica",
                "1,2,2,5,Drama;Ficção científica",
                "2,2,1,5,Terror",
        })
        void Given_valid_pages_When_calls_findAll_Then_should_return_paginated(
                final int expectedPage,
                final int expectedPerPage,
                final int expectedItemsCount,
                final long expectedTotal,
                final String expectedGenres
        ) {
            // Given
            mockGenres();
            final var expectedTerms = "";
            final var expectedSort = "name";
            final var expectedDirection = "asc";

            final var aQuery = new SearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    expectedSort,
                    expectedDirection
            );

            // When
            final var actualResult = genreMySQLGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedItemsCount, actualResult.items().size());
            int index = 0;
            for (final var expectedName : expectedGenres.split(";")) {
                final var actualName = actualResult.items().get(index).getName();
                assertEquals(expectedName, actualName);
                index++;
            }
        }
    }

    private void mockGenres() {
        genreRepository.saveAllAndFlush(List.of(
                GenreJpaEntity.from(Genre.newGenre("Comédia romântica")),
                GenreJpaEntity.from(Genre.newGenre("Ação")),
                GenreJpaEntity.from(Genre.newGenre("Drama")),
                GenreJpaEntity.from(Genre.newGenre("Terror")),
                GenreJpaEntity.from(Genre.newGenre("Ficção científica"))
        ));
    }

    private List<CategoryID> sorted(final List<CategoryID> expectedCategories) {
        return expectedCategories.stream()
                .sorted(Comparator.comparing(CategoryID::getValue))
                .toList();
    }
}
