package com.fullcycle.admin.catalogo.application.genre.create;

import java.util.List;
import java.util.Objects;

public record CreateGenreCommand(
        String name,
        List<String> categories
) {

    public CreateGenreCommand(final String name, final List<String> categories) {
        this.name = name;
        this.categories = Objects.requireNonNull(categories);
    }

    public static CreateGenreCommand with(final String aName, final List<String> aCategories) {
        return new CreateGenreCommand(aName, aCategories);
    }
}
