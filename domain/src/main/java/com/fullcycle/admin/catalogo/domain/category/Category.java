package com.fullcycle.admin.catalogo.domain.category;

import com.fullcycle.admin.catalogo.domain.ActivationStatus;
import com.fullcycle.admin.catalogo.domain.AggregateRoot;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.Objects;

public class Category extends AggregateRoot<CategoryID> implements Cloneable {

    private String name;
    private String description;
    private ActivationStatus activationStatus = ActivationStatus.ACTIVE;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Notification notification;

    private Category(
            final CategoryID anId,
            final String aName,
            final String aDescription,
            final Instant aCreatedAt,
            final Instant anUpdatedAt) {
        super(anId);
        this.name = aName;
        this.description = aDescription;
        this.createdAt = Objects.requireNonNull(aCreatedAt, "'createdAt' should not be null");
        this.updatedAt = Objects.requireNonNull(anUpdatedAt, "'updatedAt' should not be null");
        notification = Notification.create();
        validate(notification);
    }

    private Category(
            final CategoryID anId,
            final String aName,
            final String aDescription,
            final ActivationStatus anActivationStatus,
            final Instant aCreatedAt,
            final Instant anUpdatedAt) {
        this(anId, aName, aDescription, aCreatedAt, anUpdatedAt);
        Objects.requireNonNull(anActivationStatus);
        this.activationStatus = anActivationStatus;
    }

    private Category(
            final CategoryID anId,
            final String aName,
            final String aDescription,
            final ActivationStatus anActivationStatus,
            final Instant aCreatedAt,
            final Instant anUpdatedAt,
            final Instant aDeletedAt) {
        this(anId, aName, aDescription, anActivationStatus, aCreatedAt, anUpdatedAt);
        this.deletedAt = aDeletedAt;
    }

    public static Category newCategory(final String aName, final String aDescription) {
        final var aNow = InstantUtils.now();
        final CategoryID anId = CategoryID.unique();
        return new Category(anId, aName, aDescription, aNow, aNow);
    }

    public static Category with(
            final CategoryID anId,
            final String aName,
            final String aDescription,
            final ActivationStatus anActivationStatus,
            final Instant aCreatedAt,
            final Instant anUpdatedAt,
            final Instant aDeletedAt) {
        return new Category(anId, aName, aDescription, anActivationStatus, aCreatedAt, anUpdatedAt, aDeletedAt);
    }

    public static Category with(final Category aCategory) {
        final var anId = aCategory.getId();
        final var aName = aCategory.getName();
        final var aDescription = aCategory.getDescription();
        final var anActivationStatus = aCategory.getActivationStatus();
        final var aCreatedAt = aCategory.getCreatedAt();
        final var anUpdatedAt = aCategory.getUpdatedAt();
        final var aDeletedAt = aCategory.getDeletedAt();
        return with(anId, aName, aDescription, anActivationStatus, aCreatedAt, anUpdatedAt, aDeletedAt);
    }

    public Category deactivate() {
        if (getDeletedAt() == null) {
            this.deletedAt = InstantUtils.now();
        }
        this.updatedAt = InstantUtils.now();
        this.activationStatus = ActivationStatus.INACTIVE;
//        this.validate(new ThrowsValidationHandler());
        this.validate(notification);
        return this;
    }

    public Category activate() {
        this.deletedAt = null;
        this.updatedAt = InstantUtils.now();
        this.activationStatus = ActivationStatus.ACTIVE;
//        this.validate(new ThrowsValidationHandler());
        this.validate(notification);
        return this;
    }

    public Category update(final String aName, final String aDescription) {
        this.updatedAt = InstantUtils.now();
        this.name = aName;
        this.description = aDescription;
//        this.validate(new ThrowsValidationHandler());
        this.validate(notification);
        return this;
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

    public Notification getNotification() {
        return notification;
    }

    public ActivationStatus getActivationStatus() {
        return activationStatus;
    }

    public boolean hasErrors() {
        return notification.hasErrors();
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new CategoryValidator(this, handler).validate();
    }

    @Override
    public Category clone() {
        try {
            return (Category) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
