package com.fullcycle.admin.catalogo.infrastructure.genre.models;

import com.fullcycle.admin.catalogo.JacksonTest;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

@JacksonTest
class GenreListResponseTest {

    @Autowired
    private JacksonTester<GenreListResponse> json;

    @Test
    void testMarshal() throws IOException {
        // Given
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedIsActive = ActivationStatus.INACTIVE;
        final var now = InstantUtils.now();
        final var expectedCreatedAt = now;
        final var expectedDeletedAt = now;
        final var response = new GenreListResponse(
                expectedId,
                expectedName,
                expectedIsActive,
                expectedCreatedAt,
                expectedDeletedAt
        );

        // When
        final var actualJson = this.json.write(response);

        // Then
        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.activation_status", expectedIsActive)
                .hasJsonPathValue("$.created_at", expectedCreatedAt.toString())
                .hasJsonPathValue("$.deleted_at", expectedDeletedAt.toString());
    }

    @Test
    void testUnmarshal() throws IOException {
        // Given
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedIsActive = ActivationStatus.INACTIVE;
        final var now = InstantUtils.now();
        final var expectedCreatedAt = now;
        final var expectedDeletedAt = now;
        final var json = """
                {
                    "id": "%s",
                    "name": "%s",
                    "activation_status": "%s",
                    "created_at": "%s",
                    "deleted_at": "%s"
                }
                """
                .formatted(expectedId,
                        expectedName,
                        expectedIsActive,
                        expectedCreatedAt.toString(),
                        expectedDeletedAt.toString());
        // When
        final var actualJson = this.json.parse(json);
        // Then
        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("activationStatus", expectedIsActive)
                .hasFieldOrPropertyWithValue("createdAt", expectedCreatedAt)
                .hasFieldOrPropertyWithValue("deletedAt", expectedDeletedAt);
    }
}
