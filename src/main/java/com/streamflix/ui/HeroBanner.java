package com.streamflix.ui;

import com.streamflix.model.Movie;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/*
 * Displays the featured movie image, details, and trailer action.
 */
public class HeroBanner extends StackPane {

    private static final double HEIGHT = 360;

    public HeroBanner(Movie movie, Runnable onPlay) {
        getStyleClass().add("hero");
        setMinHeight(HEIGHT);
        setPrefHeight(HEIGHT);
        setMaxHeight(HEIGHT);

        // Load the large featured image from YouTube.
        Image image = new Image(movie.getBackdropUrl(), 1280, 0, true, true, true);
        ImageView backdrop = new ImageView(image);
        backdrop.setPreserveRatio(true);
        backdrop.fitWidthProperty().bind(widthProperty());
        image.errorProperty().addListener((o, was, failed) -> backdrop.setVisible(!failed));

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        Region fadeLeft = new Region();
        fadeLeft.getStyleClass().add("hero-fade-left");
        fadeLeft.setMouseTransparent(true);
        Region fadeBottom = new Region();
        fadeBottom.getStyleClass().add("hero-fade-bottom");
        fadeBottom.setMouseTransparent(true);

        // Add the featured label, title, year, description, and play control.
        Label tag = new Label("FEATURED TRAILER");
        tag.getStyleClass().add("hero-tag");
        Label title = new Label(movie.getTitle());
        title.getStyleClass().add("hero-title");
        title.setWrapText(true);
        Label meta = new Label(String.valueOf(movie.getYear()));
        meta.getStyleClass().add("hero-meta");
        Label synopsis = new Label(movie.getSynopsis());
        synopsis.getStyleClass().add("hero-synopsis");
        synopsis.setWrapText(true);

        CustomButton playButton = new CustomButton("Play Trailer",
                IconFactory.play(16, Color.web("#111111")), CustomButton.Style.PRIMARY, onPlay);
        HBox buttons = new HBox(12, playButton);
        buttons.setPadding(new Insets(8, 0, 0, 0));

        VBox info = new VBox(10, tag, title, meta, synopsis, buttons);
        info.setMaxSize(560, USE_PREF_SIZE);
        info.setPadding(new Insets(0, 0, 56, CategoryRow.PAGE_PAD));
        StackPane.setAlignment(info, Pos.BOTTOM_LEFT);

        getChildren().addAll(backdrop, fadeLeft, fadeBottom, info);

        // Fade and slide the banner information into view after the splash screen.
        info.setOpacity(0);
        info.setTranslateY(24);
        FadeTransition fade = new FadeTransition(Duration.millis(900), info);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(900), info);
        slide.setToY(0);
        ParallelTransition entrance = new ParallelTransition(fade, slide);
        entrance.setDelay(Duration.millis(1800)); 
        entrance.play();
    }
}
