package com.fullcycle.admin.catalogo.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;

import java.time.Instant;

public record GenreListResponse(
        String id,
        String name,
        @JsonProperty("activation_status") ActivationStatus activationStatus,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {
}
