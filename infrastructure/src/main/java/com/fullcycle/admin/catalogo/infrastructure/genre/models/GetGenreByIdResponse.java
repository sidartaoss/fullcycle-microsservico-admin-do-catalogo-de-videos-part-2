package com.fullcycle.admin.catalogo.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;

import java.time.Instant;
import java.util.List;

public record GetGenreByIdResponse(
        String id,
        String name,
        @JsonProperty("activation_status") ActivationStatus activationStatus,
        @JsonProperty("categories_id") List<String> categories,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {
}
