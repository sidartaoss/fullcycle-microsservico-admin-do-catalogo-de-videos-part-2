package com.fullcycle.admin.catalogo.infrastructure.category.persistence;

import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.converter.ActivationStatusToBooleanConverter;
import jakarta.persistence.*;

import java.time.Instant;

@Entity(name = "Category")
@Table(name = "category")
public class CategoryJpaEntity {

    @Id
    private String id;

    private String name;

    private String description;

    @Convert(converter = ActivationStatusToBooleanConverter.class)
    @Column(name = "active")
    private ActivationStatus activationStatus;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public CategoryJpaEntity() {
    }

    private CategoryJpaEntity(
            final String anId,
            final String aName,
            final String aDescription,
            final ActivationStatus anActivationStatus,
            final Instant aCreatedAt,
            final Instant anUpdatedAt,
            final Instant aDeletedAt
    ) {
        this.id = anId;
        this.name = aName;
        this.description = aDescription;
        this.activationStatus = anActivationStatus;
        this.createdAt = aCreatedAt;
        this.updatedAt = anUpdatedAt;
        this.deletedAt = aDeletedAt;
    }

    public static CategoryJpaEntity from(final Category aCategory) {
        final var anId = aCategory.getId().getValue();
        final var aName = aCategory.getName();
        final var aDescription = aCategory.getDescription();
        final var anActivationStatus = aCategory.getActivationStatus();
        final var aCreatedAt = aCategory.getCreatedAt();
        final var anUpdatedAt = aCategory.getUpdatedAt();
        final var aDeletedAt = aCategory.getDeletedAt();
        return new CategoryJpaEntity(anId, aName, aDescription, anActivationStatus, aCreatedAt, anUpdatedAt, aDeletedAt);
    }

    public Category toAggregate() {
        return Category.with(
                CategoryID.from(getId()),
                getName(),
                getDescription(),
                getActivationStatus(),
                getCreatedAt(),
                getUpdatedAt(),
                getDeletedAt()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActivationStatus getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(ActivationStatus activationStatus) {
        this.activationStatus = activationStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
