package com.streamflix.model;

/*
 * Stores the information needed to display one movie and open its trailer.
 */
public final class Movie {

    private final String title;
    private final int year;
    private final String youtubeId;
    private final String synopsis;

    public Movie(String title, int year, String youtubeId) {
        this(title, year, youtubeId, "");
    }

    public Movie(String title, int year, String youtubeId, String synopsis) {
        this.title = title;
        this.year = year;
        this.youtubeId = youtubeId;
        this.synopsis = synopsis;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public String getSynopsis() {
        return synopsis;
    }

    // YouTube thumbnail used on the movie card.
    public String getThumbnailUrl() {
        return "https://i.ytimg.com/vi/" + youtubeId + "/mqdefault.jpg";
    }

    // Large YouTube thumbnail used as the hero background.
    public String getBackdropUrl() {
        return "https://i.ytimg.com/vi/" + youtubeId + "/maxresdefault.jpg";
    }

    // Builds the YouTube embed address used by the in-app player.
    public String getEmbedUrl() {
        return "https://www.youtube.com/embed/" + youtubeId
                + "?autoplay=1&controls=0&rel=0&modestbranding=1&playsinline=1"
                + "&iv_load_policy=3&fs=0&disablekb=1";
    }

    
    public String getWatchUrl() {
        return "https://www.youtube.com/watch?v=" + youtubeId;
    }

    @Override
    public String toString() {
        return title + " (" + year + ")";
    }
}
