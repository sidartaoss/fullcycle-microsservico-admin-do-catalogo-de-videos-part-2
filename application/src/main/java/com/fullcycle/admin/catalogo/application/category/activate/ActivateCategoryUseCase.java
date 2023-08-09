package com.fullcycle.admin.catalogo.application.category.activate;

import com.fullcycle.admin.catalogo.application.UseCase;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class ActivateCategoryUseCase
        extends UseCase<String, Either<Notification, ActivateCategoryOutput>> {
}
