package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.application.genre.activate.ActivateGenreOutput;
import com.fullcycle.admin.catalogo.application.genre.activate.ActivateGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.create.CreateGenreOutput;
import com.fullcycle.admin.catalogo.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.deactivate.DeactivateGenreOutput;
import com.fullcycle.admin.catalogo.application.genre.deactivate.DeactivateGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.delete.DeleteGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.retrieve.get.GetGenreByIdOutput;
import com.fullcycle.admin.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.fullcycle.admin.catalogo.application.genre.retrieve.list.GenreListOutput;
import com.fullcycle.admin.catalogo.application.genre.retrieve.list.ListGenresUseCase;
import com.fullcycle.admin.catalogo.application.genre.update.UpdateGenreOutput;
import com.fullcycle.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ControllerTest(controllers = GenreAPI.class)
public class GenreAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

    @MockBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @MockBean
    private UpdateGenreUseCase updateGenreUseCase;

    @MockBean
    private ActivateGenreUseCase activateGenreUseCase;

    @MockBean
    private DeactivateGenreUseCase deactivateGenreUseCase;

    @MockBean
    private DeleteGenreUseCase deleteGenreUseCase;

    @MockBean
    private ListGenresUseCase listGenresUseCase;

    @Nested
    @DisplayName("Create a genre with valid request")
    class CreateWithValidRequest {

        @Test
        void Given_a_valid_request_When_calls_create_genre_Then_should_return_genre_id()
                throws Exception {
            // Given
            final var expectedName = "Ação";
            final var expectedCategories = List.of("456", "789");
            final var expectedId = "123";
            final var aRequest =
                    new CreateGenreRequest(expectedName, expectedCategories);

            when(createGenreUseCase.execute(any()))
                    .thenReturn(CreateGenreOutput.from(expectedId));

            final var mockMvcRequest = post("/genres")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/genres/" + expectedId))
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId)));
            verify(createGenreUseCase, times(1))
                    .execute(argThat(command ->
                            Objects.equals(expectedName, command.name())
                                    && Objects.equals(expectedCategories, command.categories())
                    ));
        }
    }

    @Nested
    @DisplayName("Create a genre with invalid request")
    class CreateWithInvalidRequest {

        @Test
        void Given_an_invalid_request_When_calls_create_genre_Then_should_return_domain_exception()
                throws Exception {
            // Given
            final var expectedName = " ";
            final var expectedCategories = List.of("456", "789");
            final var aRequest =
                    new CreateGenreRequest(expectedName, expectedCategories);

            final var expectedErrorMessage = "'name' should not be empty";

            when(createGenreUseCase.execute(any()))
                    .thenThrow(new NotificationException("Error",
                            Notification.create(new Error(expectedErrorMessage))));

            final var request = post("/genres")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(header().string("Location", nullValue()))
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
            verify(createGenreUseCase, times(1))
                    .execute(argThat(command ->
                            Objects.equals(expectedName, command.name())
                                    && Objects.equals(expectedCategories, command.categories())
                    ));
        }
    }

    @Nested
    @DisplayName("Get a genre with valid id")
    class GetGenreWithValidId {

        @Test
        void Given_a_valid_genre_id_When_calls_find_by_id_Then_should_return_genre() throws Exception {
            // Given
            final var expectedName = "Filmes";
            final var expectedCategories = List.of("123", "456");
            final var aGenre = Genre.newGenre(expectedName)
                    .addCategories(expectedCategories.stream()
                            .map(CategoryID::from)
                            .toList());
            final var expectedId = aGenre.getId().getValue();
            final var expectedIsActive = ActivationStatus.ACTIVE;

            when(getGenreByIdUseCase.execute(any()))
                    .thenReturn(GetGenreByIdOutput.from(aGenre));

            final var request = get("/genres/{id}", expectedId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId)))
                    .andExpect(jsonPath("$.name", equalTo(expectedName)))
                    .andExpect(jsonPath("$.activation_status", equalTo(expectedIsActive.toString())))
                    .andExpect(jsonPath("$.categories_id", equalTo(expectedCategories)))
                    .andExpect(jsonPath("$.created_at", equalTo(aGenre.getCreatedAt().toString())))
                    .andExpect(jsonPath("$.updated_at", equalTo(aGenre.getUpdatedAt().toString())))
                    .andExpect(jsonPath("$.deleted_at", is(nullValue())));
            verify(getGenreByIdUseCase, times(1)).execute(expectedId);
        }
    }

    @Nested
    @DisplayName("Get a genre with invalid id")
    class GetGenreWithInvalidId {

        @Test
        void Given_an_invalid_id_When_calls_find_by_id_Then_should_return_not_found() throws Exception {
            // Given
            final var expectedId = "123";
            final var expectedErrorMessage = "Genre with ID %s was not found".formatted(expectedId);

            final var request = get("/genres/{id}", expectedId)
                    .contentType(MediaType.APPLICATION_JSON);

            when(getGenreByIdUseCase.execute(expectedId))
                    .thenThrow(NotFoundException.with(Genre.class, GenreID.from(expectedId)));

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

            verify(getGenreByIdUseCase, times(1)).execute(expectedId);
        }
    }

    @Nested
    @DisplayName("Update a genre with valid request")
    class UpdateWithValidRequest {

        @Test
        void Given_a_valid_request_When_calls_update_genre_Then_should_return_genre_updated()
                throws Exception {
            // Given
            final var expectedName = "Ação";
            final var expectedCategories = List.of("456", "789");

            final var aGenre = Genre.newGenre(expectedName);
            final var expectedId = aGenre.getId().getValue();
            final var aRequest =
                    new UpdateGenreRequest(expectedName, expectedCategories);

            when(updateGenreUseCase.execute(any()))
                    .thenReturn(UpdateGenreOutput.from(expectedId));

            final var mockMvcRequest = put("/genres/{id}", expectedId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId)));
            verify(updateGenreUseCase, times(1))
                    .execute(argThat(command ->
                            Objects.equals(expectedName, command.name())
                                    && Objects.equals(expectedCategories, command.categories())
                    ));
        }
    }

    @Nested
    @DisplayName("Update a genre with invalid id")
    class UpdateWithInvalidId {

        @Test
        void Given_an_invalid_id_When_calls_update_genre_Then_should_return_not_found_exception()
                throws Exception {
            // Given
            final var expectedName = "Ação";
            final var expectedCategories = List.of("456", "789");
            final var aRequest =
                    new UpdateGenreRequest(expectedName, expectedCategories);

            final var expectedId = "not-found";
            final var expectedErrorMessage = "%s with ID %s was not found"
                    .formatted(Genre.class.getSimpleName(), expectedId);

            when(updateGenreUseCase.execute(any()))
                    .thenThrow(NotFoundException.with(
                            Genre.class, GenreID.from(expectedId)));

            final var request = put("/genres/{id}", expectedId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isNotFound())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
            verify(updateGenreUseCase, times(1))
                    .execute(argThat(command ->
                            Objects.equals(expectedName, command.name())
                                    && Objects.equals(expectedCategories, command.categories())
                    ));
        }
    }

    @Nested
    @DisplayName("Update genre with an invalid name")
    class UpdateWithInvalidName {

        @Test
        void Given_an_invalid_name_When_calls_update_category_Then_should_return_domain_exception()
                throws Exception {
            // Given
            final var expectedName = " ";
            final var expectedCategories = List.of("123", "456");
            final var aRequest =
                    new UpdateGenreRequest(expectedName, expectedCategories);

            final var aGenre = Genre.newGenre("Ação");
            final var expectedId = aGenre.getId().getValue();

            final var expectedErrorMessage = "'name' should not be empty";
            final var expectedErrorCount = 1;

            when(updateGenreUseCase.execute(any()))
                    .thenThrow(new NotificationException("Error",
                            Notification.create(new Error(expectedErrorMessage))));

            final var mockMvcRequest = put("/genres/{id}", expectedId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                    .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
            verify(updateGenreUseCase, times(1))
                    .execute(argThat(command ->
                            Objects.equals(expectedName, command.name())
                                    && Objects.equals(expectedCategories, command.categories())
                    ));
        }
    }

    @Nested
    @DisplayName("Activate genre with a valid id")
    class ActivateWithValidId {

        @Test
        void Given_a_valid_id_When_calls_activate_genre_Then_should_return_an_active_genre_id()
                throws Exception {
            // Given
            final var expectedId = "123";

            when(activateGenreUseCase.execute(anyString()))
                    .thenReturn(ActivateGenreOutput.from(expectedId));

            final var request = put("/genres/{id}/active", expectedId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId)));
            verify(activateGenreUseCase, times(1)).execute(expectedId);
        }
    }

    @Nested
    @DisplayName("Activate genre with an invalid id")
    class ActivateWithInvalidId {

        @Test
        void Given_an_invalid_id_When_calls_activate_genre_Then_should_return_not_found_exception()
                throws Exception {
            // Given
            final var expectedId = "not-found";
            final var expectedErrorMessage = "%s with ID %s was not found"
                    .formatted(Genre.class.getSimpleName(), expectedId);

            when(activateGenreUseCase.execute(any()))
                    .thenThrow(NotFoundException.with(
                            Genre.class, GenreID.from(expectedId)));

            final var request = put("/genres/{id}/active", expectedId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isNotFound())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
            verify(activateGenreUseCase, times(1)).execute(expectedId);
        }
    }

    @Nested
    @DisplayName("Deactivate genre with a valid id")
    class DeactivateWithValidId {

        @Test
        void Given_a_valid_id_When_calls_deactivate_genre_Then_should_return_an_inactivated_genre_id()
                throws Exception {
            // Given
            final var expectedId = "123";

            when(deactivateGenreUseCase.execute(anyString()))
                    .thenReturn(DeactivateGenreOutput.from(expectedId));

            final var request = put("/genres/{id}/inactive", expectedId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId)));
            verify(deactivateGenreUseCase, times(1)).execute(expectedId);
        }
    }

    @Nested
    @DisplayName("Deactivate genre with an invalid id")
    class DeactivateWithInvalidId {

        @Test
        void Given_an_invalid_id_When_calls_deactivate_genre_Then_should_return_not_found_exception()
                throws Exception {
            // Given
            final var expectedId = "not-found";
            final var expectedErrorMessage = "%s with ID %s was not found"
                    .formatted(Genre.class.getSimpleName(), expectedId);

            when(deactivateGenreUseCase.execute(any()))
                    .thenThrow(NotFoundException.with(
                            Genre.class, GenreID.from(expectedId)));

            final var request = put("/genres/{id}/inactive", expectedId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isNotFound())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
            verify(deactivateGenreUseCase, times(1)).execute(expectedId);
        }
    }

    @Nested
    @DisplayName("Delete a genre with valid id")
    class DeleteGenreWithValidId {

        @Test
        void Given_a_valid_genre_id_When_calls_delete_genre_Then_should_return_no_content() throws Exception {
            // Given
            final var expectedId = "123";

            doNothing().when(deleteGenreUseCase)
                    .execute(any());

            final var request = delete("/genres/{id}", expectedId)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isNoContent());
            verify(deleteGenreUseCase, times(1)).execute(expectedId);
        }
    }

    @Nested
    @DisplayName("List genres with valid params")
    class ListGenresWithValidParams {

        @Test
        void Given_a_valid_params_When_calls_list_genres_Then_should_return_genres()
                throws Exception {
            // Given
            final var aGenre = Genre.newGenre("Ação");
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "aç";
            final var expectedSort = "name";
            final var expectedDirection = "asc";

            final var expectedItemsCount = 1;
            final var expectedTotal = 1;

            final var expectedItems = List.of(GenreListOutput.from(aGenre));

            when(listGenresUseCase.execute(any()))
                    .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

            final var request = get("/genres")
                    .queryParam("page", String.valueOf(expectedPage))
                    .queryParam("perPage", String.valueOf(expectedPerPage))
                    .queryParam("sort", expectedSort)
                    .queryParam("dir", expectedDirection)
                    .queryParam("search", expectedTerms)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                    .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                    .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                    .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                    .andExpect(jsonPath("$.items[0].id", is(equalTo(aGenre.getId().getValue()))))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aGenre.getName()))))
                    .andExpect(jsonPath("$.items[0].activation_status", is(equalTo(aGenre.getActivationStatus().toString()))))
                    .andExpect(jsonPath("$.items[0].created_at", is(equalTo(aGenre.getCreatedAt().toString()))))
                    .andExpect(jsonPath("$.items[0].deleted_at", is(equalTo(aGenre.getDeletedAt()))));
            verify(listGenresUseCase, times(1)).execute(argThat(query ->
                    Objects.equals(expectedPage, query.page())
                            && Objects.equals(expectedPerPage, query.perPage())
                            && Objects.equals(expectedDirection, query.direction())
                            && Objects.equals(expectedSort, query.sort())
                            && Objects.equals(expectedTerms, query.terms())
            ));
        }
    }
}