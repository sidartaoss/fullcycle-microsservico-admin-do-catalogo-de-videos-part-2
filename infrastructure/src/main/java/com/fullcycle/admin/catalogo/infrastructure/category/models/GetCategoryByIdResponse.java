package com.fullcycle.admin.catalogo.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fullcycle.admin.catalogo.domain.ActivationStatus;

import java.time.Instant;

public record GetCategoryByIdResponse(
        String id,
        String name,
        String description,
        @JsonProperty("activation_status") ActivationStatus activationStatus,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt
) {
}
