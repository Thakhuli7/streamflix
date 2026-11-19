package com.streamflix.ui;

import com.streamflix.audio.SoundManager;
import com.streamflix.model.Movie;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.function.Consumer;

/*
 * Shows one movie thumbnail and opens the selected movie when clicked or activated.
 */
public class ThumbnailCard extends VBox {

    private static final double ASPECT = 9.0 / 16.0;
    private static final double HOVER_SCALE = 1.10;

    private final StackPane thumb = new StackPane();
    private final StackPane overlay = new StackPane();
    private final DropShadow glow = new DropShadow(28, Color.rgb(229, 9, 20, 0.65));

    public ThumbnailCard(Movie movie, ReadOnlyDoubleProperty cardWidth, Consumer<Movie> onSelect) {
        super(8);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("thumbnail-card");

        buildThumb(movie, cardWidth);

        Label title = new Label(movie.getTitle());
        title.getStyleClass().add("card-title");
        title.maxWidthProperty().bind(cardWidth);
        title.prefWidthProperty().bind(cardWidth);
        title.setCursor(Cursor.HAND);
        title.setFocusTraversable(true);

        getChildren().addAll(thumb, title);

        installHover();
        installClick(title, movie, onSelect);
    }

    private void buildThumb(Movie movie, ReadOnlyDoubleProperty cardWidth) {
        thumb.getStyleClass().add("thumb");
        thumb.prefWidthProperty().bind(cardWidth);
        thumb.minWidthProperty().bind(cardWidth);
        thumb.maxWidthProperty().bind(cardWidth);
        thumb.prefHeightProperty().bind(cardWidth.multiply(ASPECT));
        thumb.minHeightProperty().bind(cardWidth.multiply(ASPECT));
        thumb.maxHeightProperty().bind(cardWidth.multiply(ASPECT));

        // Round the thumbnail corners.
        Rectangle clip = new Rectangle();
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        clip.widthProperty().bind(thumb.widthProperty());
        clip.heightProperty().bind(thumb.heightProperty());
        thumb.setClip(clip);

        // Show a coloured title card if the YouTube image is unavailable.
        Region placeholder = new Region();
        placeholder.setBackground(new Background(new BackgroundFill(placeholderGradient(movie.getTitle()), null, null)));
        Label placeholderTitle = new Label(movie.getTitle());
        placeholderTitle.getStyleClass().add("placeholder-title");
        placeholderTitle.setWrapText(true);
        placeholderTitle.maxWidthProperty().bind(cardWidth.subtract(24));

        // Load the real YouTube thumbnail in the background.
        Image image = new Image(movie.getThumbnailUrl(), 480, 270, false, true, true);
        ImageView imageView = new ImageView(image);
        imageView.fitWidthProperty().bind(cardWidth);
        imageView.fitHeightProperty().bind(cardWidth.multiply(ASPECT));
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        image.errorProperty().addListener((obs, was, failed) -> {
            if (failed) {
                imageView.setVisible(false); 
            }
        });

        // The play icon and release year appear when the card is hovered.
        buildOverlay(movie);

        thumb.getChildren().addAll(placeholder, placeholderTitle, imageView, overlay);
    }

    private void buildOverlay(Movie movie) {
        overlay.getStyleClass().add("thumb-overlay");
        overlay.setOpacity(0);
        overlay.setMouseTransparent(true);

        Circle badge = new Circle(22);
        badge.setFill(Color.rgb(0, 0, 0, 0.55));
        badge.setStroke(Color.WHITE);
        badge.setStrokeWidth(2);
        StackPane playBadge = new StackPane(badge, IconFactory.play(16, Color.WHITE));

        Label year = new Label(String.valueOf(movie.getYear()));
        year.getStyleClass().add("thumb-year");
        StackPane.setAlignment(year, Pos.BOTTOM_LEFT);

        overlay.getChildren().addAll(playBadge, year);
    }

    private void installHover() {
        setCursor(Cursor.HAND);
        setFocusTraversable(true);

        setOnMouseEntered(e -> {
            // Draw this card above neighbouring cards while it is enlarged.
            setViewOrder(-1);
            animateScale(HOVER_SCALE);
            fadeOverlay(1);
            thumb.setEffect(glow);
            if (!getStyleClass().contains("hovered")) {
                getStyleClass().add("hovered");
            }
            SoundManager.getInstance().play(SoundManager.Effect.HOVER);
        });
        setOnMouseExited(e -> {
            setViewOrder(0);
            animateScale(1.0);
            fadeOverlay(0);
            thumb.setEffect(null);
            getStyleClass().remove("hovered");
        });
    }

    private void installClick(Label title, Movie movie, Consumer<Movie> onSelect) {
        installClickTarget(this, movie, onSelect);
        installClickTarget(title, movie, onSelect);
    }

    private void installClickTarget(javafx.scene.Node target, Movie movie, Consumer<Movie> onSelect) {
        target.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                onSelect.accept(movie);
            }
        });
        target.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                onSelect.accept(movie);
                e.consume();
            }
        });
    }

    private void animateScale(double target) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(180), thumb);
        transition.setToX(target);
        transition.setToY(target);
        transition.play();
    }

    private void fadeOverlay(double target) {
        FadeTransition fade = new FadeTransition(Duration.millis(180), overlay);
        fade.setToValue(target);
        fade.play();
    }

    // Creates a stable two-colour placeholder based on the movie title.
    private static LinearGradient placeholderGradient(String title) {
        double hue = Math.abs(title.hashCode()) % 360;
        return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.hsb(hue, 0.55, 0.42)),
                new Stop(1, Color.hsb((hue + 40) % 360, 0.65, 0.18)));
    }
}
