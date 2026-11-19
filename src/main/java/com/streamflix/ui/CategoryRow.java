package com.streamflix.ui;

import com.streamflix.model.Category;
import com.streamflix.model.Movie;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.function.Consumer;

/*
 * Displays one category as a labelled, horizontally scrollable row.
 */
public class CategoryRow extends VBox {

    public static final double GAP = 16;
    public static final double PAGE_PAD = 48;
    public static final int VISIBLE_CARDS = 5;

    private final ScrollPane scrollPane = new ScrollPane();
    private final HBox strip = new HBox(GAP);
    private final ReadOnlyDoubleProperty cardWidth;
    private final CustomButton leftArrow;
    private final CustomButton rightArrow;
    private final StackPane stack;
    private Timeline scrollAnimation;

    public CategoryRow(Category category, ReadOnlyDoubleProperty cardWidth, Consumer<Movie> onSelect) {
        this.cardWidth = cardWidth;
        getStyleClass().add("category-row");

        getChildren().add(buildHeader(category));

        // Create a card for every movie in this category.
        strip.setAlignment(Pos.TOP_LEFT);
        strip.setPadding(new Insets(16, PAGE_PAD, 12, PAGE_PAD));
        for (Movie movie : category.getMovies()) {
            strip.getChildren().add(new ThumbnailCard(movie, cardWidth, onSelect));
        }

        scrollPane.setContent(strip);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(false);
        scrollPane.getStyleClass().add("row-scroll");
        // Scroll bars are hidden while the arrow controls and swipe gestures remain available.
        scrollPane.prefHeightProperty().bind(cardWidth.multiply(9.0 / 16.0).add(66));
        scrollPane.minHeightProperty().bind(cardWidth.multiply(9.0 / 16.0).add(66));
        installHorizontalSwipe();

        // These custom controls provide animated page-by-page scrolling.
        leftArrow = new CustomButton(null, IconFactory.chevron(false, 18, Color.WHITE),
                CustomButton.Style.ICON, () -> scrollPage(-1));
        rightArrow = new CustomButton(null, IconFactory.chevron(true, 18, Color.WHITE),
                CustomButton.Style.ICON, () -> scrollPage(1));
        for (CustomButton arrow : new CustomButton[]{leftArrow, rightArrow}) {
            arrow.setPrefSize(40, 40);
            arrow.setOpacity(0);
            arrow.setMouseTransparent(true);
        }
        StackPane.setAlignment(leftArrow, Pos.CENTER_LEFT);
        StackPane.setAlignment(rightArrow, Pos.CENTER_RIGHT);
        StackPane.setMargin(leftArrow, new Insets(0, 0, 26, 4));
        StackPane.setMargin(rightArrow, new Insets(0, 4, 26, 0));

        stack = new StackPane(scrollPane, leftArrow, rightArrow);
        getChildren().add(stack);

        // Show the arrows only when the row is hovered and more movies are available.
        stack.hoverProperty().addListener((o, a, b) -> refreshArrows());
        scrollPane.hvalueProperty().addListener((o, a, b) -> refreshArrows());
        strip.widthProperty().addListener((o, a, b) -> refreshArrows());
    }

    private HBox buildHeader(Category category) {
        Rectangle accent = new Rectangle(4, 24);
        accent.setFill(Color.web("#E50914"));
        accent.setArcWidth(4);
        accent.setArcHeight(4);

        Label name = new Label(category.getName());
        name.getStyleClass().add("row-title");
        Label count = new Label(category.getMovies().size() + " titles");
        count.getStyleClass().add("row-count");

        HBox header = new HBox(12, accent, name, count);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 0, PAGE_PAD));
        return header;
    }

    // Moves the row by five cards in the selected direction.
    private void scrollPage(int direction) {
        double maxPx = maxScrollPixels();
        if (maxPx <= 0) {
            return;
        }
        double step = VISIBLE_CARDS * (cardWidth.get() + GAP);
        double currentPx = scrollPane.getHvalue() * maxPx;
        double targetPx = Math.max(0, Math.min(maxPx, currentPx + direction * step));

        if (scrollAnimation != null) {
            scrollAnimation.stop();
        }
        scrollAnimation = new Timeline(new KeyFrame(Duration.millis(450),
                new KeyValue(scrollPane.hvalueProperty(), targetPx / maxPx, Interpolator.EASE_BOTH)));
        scrollAnimation.play();
    }

    // Allows horizontal mouse-wheel or trackpad swipes inside the category row.
    private void installHorizontalSwipe() {
        scrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (Math.abs(e.getDeltaX()) > Math.abs(e.getDeltaY())) {
                double maxPx = maxScrollPixels();
                if (maxPx > 0) {
                    double h = scrollPane.getHvalue() - e.getDeltaX() / maxPx;
                    scrollPane.setHvalue(Math.max(0, Math.min(1, h)));
                }
                e.consume();
            }
        });
    }

    private double maxScrollPixels() {
        return strip.getWidth() - scrollPane.getViewportBounds().getWidth();
    }

    private void refreshArrows() {
        boolean scrollable = maxScrollPixels() > 1;
        boolean showLeft = stack.isHover() && scrollable && scrollPane.getHvalue() > 0.01;
        boolean showRight = stack.isHover() && scrollable && scrollPane.getHvalue() < 0.99;
        fadeArrow(leftArrow, showLeft);
        fadeArrow(rightArrow, showRight);
    }

    private void fadeArrow(CustomButton arrow, boolean show) {
        double target = show ? 1 : 0;
        if (arrow.getOpacity() == target) {
            return;
        }
        arrow.setMouseTransparent(!show);
        FadeTransition fade = new FadeTransition(Duration.millis(160), arrow);
        fade.setToValue(target);
        fade.play();
    }
}
