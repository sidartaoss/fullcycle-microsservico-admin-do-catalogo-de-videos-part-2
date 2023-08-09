package com.fullcycle.admin.catalogo.application.genre.update;

import java.util.List;

public record UpdateGenreCommand(
        String id,
        String name,
        List<String> categories
) {

    public static UpdateGenreCommand with(
            final String anId,
            final String aName,
            final List<String> aCategories) {
        return new UpdateGenreCommand(anId, aName, aCategories);
    }
}
