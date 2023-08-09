package com.fullcycle.admin.catalogo.e2e;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.GetCategoryByIdResponse;
import com.fullcycle.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.configuration.json.Json;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GetGenreByIdResponse;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockDsl {

    MockMvc mvc();

    default ResultActions deleteACategory(final Identifier anId) throws Exception {
        return this.delete("/categories/", anId);
    }

    default ResultActions deleteAGenre(final Identifier anId) throws Exception {
        return this.delete("/genres/", anId);
    }

    default GetCategoryByIdResponse retrieveACategory(final Identifier anId) throws Exception {
        return this.retrieve("/categories/", anId, GetCategoryByIdResponse.class);
    }

    default ResultActions updateACategory(
            final Identifier anId,
            final UpdateCategoryRequest aRequest) throws Exception {
        return this.update("/categories/", anId, aRequest);
    }

    default ResultActions activateACategory(
            final Identifier anId) throws Exception {
        return this.update("/categories/%s/active", anId);
    }

    default ResultActions deactivateACategory(
            final Identifier anId) throws Exception {
        return this.update("/categories/%s/inactive", anId);
    }

    default ResultActions updateAGenre(
            final Identifier anId,
            final UpdateGenreRequest aRequest) throws Exception {
        return this.update("/genres/", anId, aRequest);
    }

    default ResultActions activateAGenre(
            final Identifier anId) throws Exception {
        return this.update("/genres/%s/active", anId);
    }

    default ResultActions deactivateAGenre(
            final Identifier anId) throws Exception {
        return this.update("/genres/%s/inactive", anId);
    }

    default GetGenreByIdResponse retrieveAGenre(final Identifier anId) throws Exception {
        return this.retrieve("/genres/", anId, GetGenreByIdResponse.class);
    }

    default ResultActions listCategories(
            final int page,
            final int perPage) throws Exception {
        return this.listCategories(page, perPage, "", "", "");
    }

    default ResultActions listCategories(
            final int page,
            final int perPage,
            final String search) throws Exception {
        return this.listCategories(page, perPage, search, "", "");
    }

    default ResultActions listCategories(
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String direction) throws Exception {
        return this.list("/categories", page, perPage, search, sort, direction);
    }

    default ResultActions listGenres(
            final int page,
            final int perPage) throws Exception {
        return this.listGenres(page, perPage, "", "", "");
    }

    default ResultActions listGenres(
            final int page,
            final int perPage,
            final String search) throws Exception {
        return this.listGenres(page, perPage, search, "", "");
    }

    default ResultActions listGenres(
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String direction) throws Exception {
        return this.list("/genres", page, perPage, search, sort, direction);
    }

    private ResultActions list(
            final String url,
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String direction) throws Exception {
        final var aRequest = get(url)
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("dir", direction)
                .contentType(MediaType.APPLICATION_JSON);
        return this.mvc().perform(aRequest);
    }

    default CategoryID givenACategory(final String aName, final String aDescription) throws Exception {
        final var aRequestBody = new CreateCategoryRequest(aName, aDescription);
        final var actualId = this.given("/categories", aRequestBody);
        return CategoryID.from(actualId);
    }

    default GenreID givenAGenre(final String aName, final List<CategoryID> aCategories) throws Exception {
        final var aRequestBody = new CreateGenreRequest(aName, mapTo(aCategories, CategoryID::getValue));
        final var actualId = this.given("/genres", aRequestBody);
        return GenreID.from(actualId);
    }

    default <A, D> List<D> mapTo(final List<A> actual, final Function<A, D> mapper) {
        return actual.stream()
                .map(mapper)
                .toList();
    }

    private String given(final String url, final Object body) throws Exception {
        final var aRequest = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        final var actualId = this.mvc().perform(aRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("%s/".formatted(url), "");

        return actualId;
    }

    private <T> T retrieve(
            final String url,
            final Identifier anId,
            final Class<T> clazz) throws Exception {
        final var aRequest = get(url + anId.getValue())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        final var json = this.mvc().perform(aRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, clazz);
    }

    private ResultActions delete(
            final String url,
            final Identifier anId) throws Exception {
        final var aRequest = MockMvcRequestBuilders.delete(url + anId.getValue())
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(aRequest);
    }

    private ResultActions update(
            final String url,
            final Identifier anId) throws Exception {
        final var aRequest = MockMvcRequestBuilders.put(url.formatted(anId.getValue()))
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(aRequest);
    }

    private ResultActions update(
            final String url,
            final Identifier anId,
            final Object aRequestBody) throws Exception {
        final var aRequest = MockMvcRequestBuilders.put(url + anId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        return this.mvc().perform(aRequest);
    }
}
