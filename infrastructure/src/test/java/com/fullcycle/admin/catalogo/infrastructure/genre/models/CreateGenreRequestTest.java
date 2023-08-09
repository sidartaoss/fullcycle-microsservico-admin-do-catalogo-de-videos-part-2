package com.fullcycle.admin.catalogo.infrastructure.genre.models;

import com.fullcycle.admin.catalogo.JacksonTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.List;

@JacksonTest
class CreateGenreRequestTest {

    @Autowired
    private JacksonTester<CreateGenreRequest> json;

    @Test
    void testMarshal() throws IOException {
        // Given
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123", "456");
        final var request = new CreateGenreRequest(
                expectedName,
                expectedCategories
        );
        // When
        final var actualJson = this.json.write(request);
        // Then
        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.categories_id", expectedCategories);
    }

    @Test
    void testUnmarshal() throws IOException {
        // Given
        final var expectedName = "Ação";
        final var expectedCategories = "123, 456";
        final var json = """
                {
                    "name": "%s",
                    "categories_id": ["%s"]
                }
                """
                .formatted(
                        expectedName,
                        expectedCategories);
        // When
        final var actualJson = this.json.parse(json);
        // Then
        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("categories", List.of(expectedCategories));
    }
}
