package com.fullcycle.admin.catalogo.infrastructure.genre.persistence;

import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.converter.ActivationStatusToBooleanConverter;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "genres")
public class GenreJpaEntity {

    @Id
    private String id;

    private String name;

    @Convert(converter = ActivationStatusToBooleanConverter.class)
    @Column(name = "active")
    private ActivationStatus activationStatus;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<GenreCategoryJpaEntity> categories = new HashSet<>();

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public GenreJpaEntity() {
    }

    public GenreJpaEntity(final String anId,
                          final String aName,
                          final ActivationStatus anActivationStatus,
                          final Instant aCreatedAt,
                          final Instant anUpdatedAt,
                          final Instant aDeletedAt) {
        this.id = anId;
        this.name = aName;
        this.activationStatus = anActivationStatus;
        this.createdAt = aCreatedAt;
        this.updatedAt = anUpdatedAt;
        this.deletedAt = aDeletedAt;
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

    public ActivationStatus getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(ActivationStatus activationStatus) {
        this.activationStatus = activationStatus;
    }

    public Set<GenreCategoryJpaEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<GenreCategoryJpaEntity> categories) {
        this.categories = categories;
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

    public static GenreJpaEntity from(final Genre aGenre) {
        final var anId = aGenre.getId().getValue();
        final var aName = aGenre.getName();
        final var anActivationStatus = aGenre.getActivationStatus();
        final var aCreateAt = aGenre.getCreatedAt();
        final var anUpdatedAt = aGenre.getUpdatedAt();
        final var aDeletedAt = aGenre.getDeletedAt();
        final var anEntity = new GenreJpaEntity(anId, aName, anActivationStatus, aCreateAt, anUpdatedAt, aDeletedAt);
        aGenre.getCategories().forEach(anEntity::addCategory);
        return anEntity;
    }

    public Genre toAggregate() {
        final var anId = GenreID.from(getId());
        final var categories = getCategoryIDs();
        return Genre.with(
                anId, getName(), getActivationStatus(), categories, getCreatedAt(), getUpdatedAt(), getDeletedAt());
    }

    public List<CategoryID> getCategoryIDs() {
        return getCategories().stream()
                .map(it -> CategoryID.from(it.getId().getCategoryId()))
                .toList();
    }

    private void addCategory(final CategoryID anId) {
        this.categories.add(GenreCategoryJpaEntity.from(this, anId));
    }

    private void removeCategory(final CategoryID anId) {
        this.categories.remove(GenreCategoryJpaEntity.from(this, anId));
    }
}
