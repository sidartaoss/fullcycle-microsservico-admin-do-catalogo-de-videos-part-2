package com.fullcycle.admin.catalogo.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;

import java.time.Instant;

public record CategoryListResponse(
        String id,
        String name,
        String description,
        @JsonProperty("activation_status") ActivationStatus activationStatus,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {
}
