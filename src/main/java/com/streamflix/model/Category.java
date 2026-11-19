package com.streamflix.model;

import java.util.Collections;
import java.util.List;

/*
 * Stores a category name and the movies shown in that category row.
 */
public final class Category {

    private final String name;
    private final List<Movie> movies;

    public Category(String name, List<Movie> movies) {
        this.name = name;
        this.movies = Collections.unmodifiableList(movies);
    }

    public String getName() {
        return name;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
