package com.fullcycle.admin.catalogo.e2e.genre;

import com.fullcycle.admin.catalogo.E2ETest;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.e2e.MockDsl;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@E2ETest
@Testcontainers
class GenreE2ETest implements MockDsl {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GenreRepository genreRepository;

    @Override
    public MockMvc mvc() {
        return this.mockMvc;
    }

    @Container
    private static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:latest")
            .withPassword("123456")
            .withUsername("root")
            .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        final var mappedPort = MYSQL_CONTAINER.getMappedPort(3306);
        System.out.printf("Container is running on port %s\n", mappedPort);
        registry.add("mysql.port", () -> mappedPort);
    }

    @Nested
    @DisplayName("Create genre with valid values")
    class CreateWithValidValues {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_create_a_new_genre_with_valid_values() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var expectedName = "Ação";
            final var expectedCategories = List.<CategoryID>of();
            final var expectedIsActive = ActivationStatus.ACTIVE;

            // When
            final var actualId = givenAGenre(expectedName, expectedCategories);

            // Then
            final var actualGenre = retrieveAGenre(actualId);

            assertEquals(expectedName, actualGenre.name());
            assertEquals(expectedIsActive, actualGenre.activationStatus());
            assertEquals(mapTo(expectedCategories, CategoryID::getValue), actualGenre.categories());
            assertNotNull(actualGenre.createdAt());
            assertNotNull(actualGenre.updatedAt());
            assertNull(actualGenre.deletedAt());
        }

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_create_a_new_genre_with_categories() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var filmes = givenACategory("Filmes", " ");

            final var expectedName = "Ação";
            final var expectedCategories = List.of(filmes);
            final var expectedIsActive = ActivationStatus.ACTIVE;

            // When
            final var actualId = givenAGenre(expectedName, expectedCategories);

            // Then
            final var actualGenre = retrieveAGenre(actualId);

            assertEquals(expectedName, actualGenre.name());
            assertEquals(expectedIsActive, actualGenre.activationStatus());
            assertEquals(mapTo(expectedCategories, CategoryID::getValue), actualGenre.categories());
            assertNotNull(actualGenre.createdAt());
            assertNotNull(actualGenre.updatedAt());
            assertNull(actualGenre.deletedAt());
        }
    }

    @Nested
    @DisplayName("List genres")
    class ListGenres {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_navigate_through_all_genres() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var aNameAcao = "Ação";
            final var aNameEsportes = "Esportes";
            final var aNameDrama = "Drama";

            givenAGenre(aNameAcao, List.of());
            givenAGenre(aNameEsportes, List.of());
            givenAGenre(aNameDrama, List.of());

            var expectedPage = 0;
            final var expectedPerPage = 1;
            final var expectedTotal = 3;

            // When & Then
            listGenres(expectedPage, expectedPerPage)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(expectedPerPage)))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aNameAcao))));

            expectedPage = 1;
            listGenres(expectedPage, expectedPerPage)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(expectedPerPage)))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aNameDrama))));

            expectedPage = 2;
            listGenres(expectedPage, expectedPerPage)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(expectedPerPage)))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aNameEsportes))));

            expectedPage = 3;
            listGenres(expectedPage, expectedPerPage)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_search_between_all_genres() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var aNameAcao = "Ação";
            final var aNameEsportes = "Esportes";
            final var aNameDrama = "Drama";

            givenAGenre(aNameAcao, List.of());
            givenAGenre(aNameEsportes, List.of());
            givenAGenre(aNameDrama, List.of());

            var expectedPage = 0;
            final var expectedPerPage = 1;
            final var expectedTotal = 1;

            // When & Then
            listGenres(expectedPage, expectedPerPage, "dra")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(expectedPerPage)))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aNameDrama))));

        }

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_sort_all_genres_by_name_desc() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var aNameAcao = "Ação";
            final var aNameEsportes = "Esportes";
            final var aNameDrama = "Drama";

            givenAGenre(aNameAcao, List.of());
            givenAGenre(aNameEsportes, List.of());
            givenAGenre(aNameDrama, List.of());

            var expectedPage = 0;
            final var expectedPerPage = 3;
            final var expectedTotal = 3;

            // When & Then
            listGenres(expectedPage, expectedPerPage, "", "name", "desc")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(expectedPerPage)))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aNameEsportes))))
                    .andExpect(jsonPath("$.items[1].name", is(equalTo(aNameDrama))))
                    .andExpect(jsonPath("$.items[2].name", is(equalTo(aNameAcao))));
        }
    }

    @Nested
    @DisplayName("Get a genre with valid id")
    class GetWithValidId {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_get_a_genre_by_its_identifier() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final var filmes = givenACategory("Filmes", " ");
            final var series = givenACategory("Séries", " ");
            final var expectedCategories = List.of(filmes, series);

            // When
            final var actualId = givenAGenre(expectedName, expectedCategories);

            // Then
            genreRepository.findById(actualId.getValue())
                    .ifPresent(actualCategory -> {

                        assertEquals(expectedName, actualCategory.getName());
                        assertEquals(expectedIsActive, actualCategory.getActivationStatus());
                        assertTrue(expectedCategories.size() == actualCategory.getCategoryIDs().size()
                                && expectedCategories.containsAll(actualCategory.getCategoryIDs()));
                        assertNotNull(actualCategory.getCreatedAt());
                        assertNotNull(actualCategory.getUpdatedAt());
                        assertNull(actualCategory.getDeletedAt());
                    });
        }
    }

    @Nested
    @DisplayName("Get a genre with invalid id")
    class GetWithInvalidId {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_see_a_treated_error_by_getting_a_not_found_genre()
                throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var expectedId = "123";
            final var aRequest = get("/genres/{id}", expectedId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON);
            final var expectedErrorMessage = "Genre with ID %s was not found"
                    .formatted(expectedId);

            // When & Then
            mockMvc.perform(aRequest)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", is(equalTo(expectedErrorMessage))));
        }
    }

    @Nested
    @DisplayName("Update a genre with valid request")
    class UpdateWithValidRequest {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_update_a_genre_by_its_identifier() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final var filmes = givenACategory("Filmes", " ");
            final var series = givenACategory("Séries", " ");
            final var expectedCategories = List.of(filmes, series);

            final var actualId = givenAGenre("acao", expectedCategories);

            final var aRequestBody = new UpdateGenreRequest(expectedName,
                    mapTo(expectedCategories, CategoryID::getValue));

            // when
            updateAGenre(actualId, aRequestBody)
                    .andExpect(status().isOk());

            // Then
            genreRepository.findById(actualId.getValue())
                    .ifPresent(actualGenre -> {

                        assertEquals(expectedName, actualGenre.getName());
                        assertEquals(expectedIsActive, actualGenre.getActivationStatus());
                        assertTrue(expectedCategories.size() == actualGenre.getCategoryIDs().size()
                                && expectedCategories.containsAll(actualGenre.getCategoryIDs()));
                        assertNotNull(actualGenre.getCreatedAt());
                        assertNotNull(actualGenre.getUpdatedAt());
                        assertNull(actualGenre.getDeletedAt());
                    });
        }
    }

    @Nested
    @DisplayName("Activate a genre with valid id")
    class ActivateWithValidId {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_activate_a_genre_by_its_identifier() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.ACTIVE;

            final var filmes = givenACategory("Filmes", " ");
            final var series = givenACategory("Séries", " ");
            final var expectedCategories = List.of(filmes, series);

            final var actualId = givenAGenre(expectedName, expectedCategories);

            deactivateAGenre(actualId)
                    .andExpect(status().isOk());

            // When
            activateAGenre(actualId)
                    .andExpect(status().isOk());

            // Then
            genreRepository.findById(actualId.getValue())
                    .ifPresent(actualGenre -> {

                        assertEquals(expectedName, actualGenre.getName());
                        assertEquals(expectedIsActive, actualGenre.getActivationStatus());
                        assertTrue(expectedCategories.size() == actualGenre.getCategoryIDs().size()
                                && expectedCategories.containsAll(actualGenre.getCategoryIDs()));
                        assertNotNull(actualGenre.getCreatedAt());
                        assertNotNull(actualGenre.getUpdatedAt());
                        assertNull(actualGenre.getDeletedAt());
                    });
        }
    }

    @Nested
    @DisplayName("Deactivate a category with valid id")
    class DeactivateWithValidId {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_deactivate_a_category_by_its_identifier() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var expectedName = "Ação";
            final var expectedIsActive = ActivationStatus.INACTIVE;

            final var filmes = givenACategory("Filmes", " ");
            final var series = givenACategory("Séries", " ");
            final var expectedCategories = List.of(filmes, series);

            final var actualId = givenAGenre(expectedName, expectedCategories);

            // When
            deactivateAGenre(actualId)
                    .andExpect(status().isOk());

            // Then
            genreRepository.findById(actualId.getValue())
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
    @DisplayName("Delete a genre with valid id")
    class DeleteWithValidId {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_delete_a_genre_by_its_identifier() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var expectedName = "Ação";

            final var filmes = givenACategory("Filmes", " ");
            final var series = givenACategory("Séries", " ");
            final var expectedCategories = List.of(filmes, series);

            final var actualId = givenAGenre(expectedName, expectedCategories);

            // When
            deleteAGenre(actualId)
                    .andExpect(status().isNoContent());

            // Then
            final var actualGenre = genreRepository.findById(actualId.getValue());
            assertTrue(actualGenre.isEmpty());
            assertEquals(0, genreRepository.count());
        }
    }

    @Nested
    @DisplayName("Delete a genre with invalid id")
    class DeleteWithInvalidId {

        @Test
        void As_a_Catalog_Admin_I_Should_not_see_an_error_by_deleting_a_non_existing_genre() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, genreRepository.count());

            // Given
            final var actualId = GenreID.from("123");

            // When
            deleteAGenre(actualId)
                    .andExpect(status().isNoContent());

            // Then
            assertEquals(0, genreRepository.count());
        }
    }
}
