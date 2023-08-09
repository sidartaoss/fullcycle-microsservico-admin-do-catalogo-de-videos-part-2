package com.fullcycle.admin.catalogo.domain.genre;

import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.AggregateRoot;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Genre extends AggregateRoot<GenreID> {

    private String name;
    private ActivationStatus activationStatus = ActivationStatus.ACTIVE;
    private List<CategoryID> categories = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Genre(
            final GenreID anId,
            final String aName,
            final Instant anCreatedAt,
            final Instant anUpdatedAt) {
        super(anId);
        this.name = aName;
        this.createdAt = Objects.requireNonNull(anCreatedAt);
        this.updatedAt = Objects.requireNonNull(anUpdatedAt);
        selfValidate();
    }

    private Genre(
            final GenreID anId,
            final String aName,
            final List<CategoryID> aCategories,
            final Instant aCreatedAt,
            final Instant anUpdatedAt) {
        this(anId, aName, aCreatedAt, anUpdatedAt);
        Objects.requireNonNull(aCategories);
        this.categories = aCategories;
    }

    private Genre(
            final GenreID anId,
            final String aName,
            final ActivationStatus anActivationStatus,
            final List<CategoryID> aCategories,
            final Instant aCreatedAt,
            final Instant anUpdatedAt) {
        this(anId, aName, aCategories, aCreatedAt, anUpdatedAt);
        Objects.requireNonNull(anActivationStatus);
        this.activationStatus = anActivationStatus;
    }

    private Genre(
            final GenreID anId,
            final String aName,
            final ActivationStatus anActivationStatus,
            final List<CategoryID> aCategories,
            final Instant anCreatedAt,
            final Instant anUpdatedAt,
            final Instant aDeletedAt) {
        this(anId, aName, anActivationStatus, aCategories, anCreatedAt, anUpdatedAt);
        this.deletedAt = aDeletedAt;
    }

    public static Genre newGenre(final String aName) {
        final var aNow = InstantUtils.now();
        final var anId = GenreID.unique();
        return new Genre(anId, aName, aNow, aNow);
    }

    public static Genre with(
            final GenreID anId,
            final String aName,
            final ActivationStatus activationStatus,
            final List<CategoryID> aCategories,
            final Instant anCreatedAt,
            final Instant anUpdatedAt,
            final Instant aDeletedAt) {
        return new Genre(anId, aName, activationStatus, aCategories, anCreatedAt, anUpdatedAt, aDeletedAt);
    }

    public static Genre with(final Genre aGenre) {
        final var anId = aGenre.getId();
        final var aName = aGenre.getName();
        final var activationStatus = aGenre.getActivationStatus();
        final var aCategories = new ArrayList<>(aGenre.getCategories());
        final var aCreatedAt = aGenre.getCreatedAt();
        final var aUpdatedAt = aGenre.getUpdatedAt();
        final var aDeletedAt = aGenre.getDeletedAt();
        return new Genre(anId, aName, activationStatus, aCategories, aCreatedAt, aUpdatedAt, aDeletedAt);
    }

    public String getName() {
        return name;
    }

    public ActivationStatus getActivationStatus() {
        return activationStatus;
    }

    public List<CategoryID> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public Genre deactivate() {
        if (getDeletedAt() == null) {
            this.deletedAt = InstantUtils.now();
        }
        this.updatedAt = InstantUtils.now();
        this.activationStatus = ActivationStatus.INACTIVE;
        selfValidate();
        return this;
    }

    public Genre activate() {
        this.deletedAt = null;
        this.updatedAt = InstantUtils.now();
        this.activationStatus = ActivationStatus.ACTIVE;
        selfValidate();
        return this;
    }

    public Genre update(final String aName, final List<CategoryID> aCategories) {
        Objects.requireNonNull(aCategories);
        this.updatedAt = InstantUtils.now();
        this.name = aName;
        this.categories = new ArrayList<>(aCategories);
        selfValidate();
        return this;
    }

    public Genre addCategory(final CategoryID aCategoryID) {
        if (aCategoryID == null) {
            return this;
        }
        final var updatedCategories = new ArrayList<>(this.categories);
        updatedCategories.add(aCategoryID);
        this.categories = updatedCategories;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre addCategories(final List<CategoryID> aCategories) {
        if (aCategories.isEmpty()) {
            return this;
        }
        final var updatedCategories = new ArrayList<>(this.categories);
        updatedCategories.addAll(aCategories);
        this.categories = updatedCategories;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre removeCategory(final CategoryID aCategoryID) {
        if (aCategoryID == null) {
            return this;
        }
        final var updatedCategories = new ArrayList<>(this.categories);
        updatedCategories.remove(aCategoryID);
        this.categories = updatedCategories;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new GenreValidator(this, handler).validate();
    }

    private void selfValidate() {
        final var notification = Notification.create();
        validate(notification);
        if (notification.hasErrors()) {
            throw new NotificationException("Failed to validate an Aggregate Genre", notification);
        }
    }
}
