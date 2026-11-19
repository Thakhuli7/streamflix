package com.streamflix.data;

import com.streamflix.model.Category;
import com.streamflix.model.Movie;

import java.util.List;

/*
 * The demo movie catalogue.
 * It contains four categories, and every category contains ten movies.
 */
public final class MovieCatalog {
    // This movie is shown in the large banner at the top of the home screen.
    private MovieCatalog() {
    }

    
    public static Movie getFeatured() {
        return new Movie("The Dark Knight", 2008, "EXeTwQWrcwY",
                "Batman, Gordon and Harvey Dent take on Gotham's mob until the Joker's "
                        + "chaos pushes the city - and the Dark Knight - to the edge.");
    }

    public static List<Category> getCategories() {
        return List.of(
                new Category("Action & Adventure", List.of(
                        m("The Dark Knight", 2008, "EXeTwQWrcwY"),
                        m("Mad Max: Fury Road", 2015, "hEJnMQG9ev8"),
                        m("John Wick", 2014, "C0BMx-qxsP4"),
                        m("Mission: Impossible - Fallout", 2018, "wb49-oV0F78"),
                        m("Gladiator", 2000, "owK1qxDselE"),
                        m("Top Gun: Maverick", 2022, "giXco2jaZ_4"),
                        m("Black Panther", 2018, "xjDjIWPwcPU"),
                        m("Die Hard", 1988, "jaJuwKCmJbY"),
                        m("Avengers: Endgame", 2019, "TcMBFSGVi1c"),
                        m("Guardians of the Galaxy", 2014, "d96cjJhvlMA"))),

                new Category("Sci-Fi & Fantasy", List.of(
                        m("Inception", 2010, "YoHD9XEInc0"),
                        m("Interstellar", 2014, "zSWdZVtXT7E"),
                        m("The Matrix", 1999, "vKQi3bBA1y8"),
                        m("Blade Runner 2049", 2017, "gCcx85zbxz4"),
                        m("Dune", 2021, "n9xhJrPXop4"),
                        m("Arrival", 2016, "tFMo3UJ4B4g"),
                        m("Ex Machina", 2014, "EoQuVnKhxaM"),
                        m("Edge of Tomorrow", 2014, "vw61gCe2oqI"),
                        m("Avatar", 2009, "5PSNL1qE6VY"),
                        m("Spider-Man: Into the Spider-Verse", 2018, "g4Hbz2jLxvQ"))),

                new Category("Animation & Family", List.of(
                        m("Toy Story", 1995, "v-PjgYDrg70"),
                        m("Finding Nemo", 2003, "wZdpNglLbt8"),
                        m("The Lion King", 1994, "lFzVJEksoDY"),
                        m("Coco", 2017, "Rvr68u6k5sI"),
                        m("Inside Out", 2015, "seMwpP0yeu4"),
                        m("Up", 2009, "ORFWdXl_zJ4"),
                        m("Frozen", 2013, "TbQm5doF_Uc"),
                        m("Shrek", 2001, "CwXOrWvPBPk"),
                        m("Kung Fu Panda", 2008, "PXi3Mv6KMzY"),
                        m("Moana", 2016, "LKFuXETZUsI"))),

                new Category("Thriller & Horror", List.of(
                        m("Get Out", 2017, "DzfpyUB60YY"),
                        m("A Quiet Place", 2018, "WR7cc5t7tv8"),
                        m("The Conjuring", 2013, "k10ETZ41q5o"),
                        m("Hereditary", 2018, "V6wWKNij_1M"),
                        m("Parasite", 2019, "5xH0HfJHsaY"),
                        m("Se7en", 1995, "znmZoVkCjpI"),
                        m("The Shining", 1980, "5Cb3ik6zP2I"),
                        m("It", 2017, "FnCdOQsX5kc"),
                        m("Joker", 2019, "zAGVQLHvwOY"),
                        m("Shutter Island", 2010, "5iaYLCiq5RM")))
        );
    }

    private static Movie m(String title, int year, String youtubeId) {
        return new Movie(title, year, youtubeId);
    }
}
