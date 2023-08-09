package com.fullcycle.admin.catalogo.infrastructure.genre.models;

import com.fullcycle.admin.catalogo.JacksonTest;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@JacksonTest
class GetGenreByIdResponseTest {

    @Autowired
    private JacksonTester<GetGenreByIdResponse> json;

    @Test
    void testMarshal() throws IOException {
        // Given
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedIsActive = ActivationStatus.INACTIVE;
        final var expectedCategories = List.of("123", "456");
        final var now = InstantUtils.now();
        final var expectedCreatedAt = now;
        final var expectedUpdatedAt = now;
        final var expectedDeletedAt = now;
        final var response = new GetGenreByIdResponse(
                expectedId,
                expectedName,
                expectedIsActive,
                expectedCategories,
                expectedCreatedAt,
                expectedUpdatedAt,
                expectedDeletedAt
        );
        // When
        final var actualJson = this.json.write(response);
        // Then
        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.activation_status", expectedIsActive)
                .hasJsonPathValue("$.categories_id", expectedCategories)
                .hasJsonPathValue("$.created_at", expectedCreatedAt.toString())
                .hasJsonPathValue("$.updated_at", expectedUpdatedAt.toString())
                .hasJsonPathValue("$.deleted_at", expectedDeletedAt.toString());
    }

    @Test
    void testUnmarshal() throws IOException {
        // Given
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedIsActive = ActivationStatus.INACTIVE;
        final var expectedCategories = "456, 789";
        final var now = Instant.now();
        final var expectedCreatedAt = now;
        final var expectedUpdatedAt = now;
        final var expectedDeleteddAt = now;
        final var json = """
                {
                    "id": "%s",
                    "name": "%s",
                    "activation_status": "%s",
                    "categories_id": ["%s"],
                    "created_at": "%s",
                    "updated_at": "%s",
                    "deleted_at": "%s"
                }
                """
                .formatted(expectedId,
                        expectedName,
                        expectedIsActive,
                        expectedCategories,
                        expectedCreatedAt.toString(),
                        expectedUpdatedAt.toString(),
                        expectedDeleteddAt.toString());
        // When
        final var actualJson = this.json.parse(json);
        // Then
        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("activationStatus", expectedIsActive)
                .hasFieldOrPropertyWithValue("categories", List.of(expectedCategories))
                .hasFieldOrPropertyWithValue("createdAt", expectedCreatedAt)
                .hasFieldOrPropertyWithValue("updatedAt", expectedUpdatedAt)
                .hasFieldOrPropertyWithValue("deletedAt", expectedDeleteddAt);
    }
}
