package com.streamflix.ui;

import com.streamflix.data.MovieCatalog;
import com.streamflix.model.Category;
import com.streamflix.model.Movie;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/*
 * Builds the complete browse screen and calculates a responsive five-card width.
 */
public class HomeView extends BorderPane {

    private final ScrollPane pageScroll = new ScrollPane();
    private final VBox content = new VBox(8);
    private final Map<Category, CategoryRow> rows = new LinkedHashMap<>();

    // This shared width makes exactly five cards fit across the page.
    private final DoubleProperty cardWidth = new SimpleDoubleProperty(200);

    public HomeView(Consumer<Movie> onSelect) {
        getStyleClass().add("home-view");

        List<Category> categories = MovieCatalog.getCategories();

        // The available width is divided across five cards, with gaps and padding included.
        cardWidth.bind(Bindings.max(120.0,
                content.widthProperty().subtract(2 * CategoryRow.PAGE_PAD + 4 * CategoryRow.GAP).divide(5)));

        content.getStyleClass().add("page-content");
        content.getChildren().add(new HeroBanner(MovieCatalog.getFeatured(), () -> onSelect.accept(MovieCatalog.getFeatured())));

        for (Category category : categories) {
            CategoryRow row = new CategoryRow(category, cardWidth, onSelect);
            rows.put(category, row);
            content.getChildren().add(row);
        }

        Label footer = new Label("StreamFlix is a student demo project. Trailers are streamed from YouTube.");
        footer.getStyleClass().add("footer");
        VBox.setMargin(footer, new Insets(24, 0, 32, CategoryRow.PAGE_PAD));
        content.getChildren().add(footer);

        pageScroll.setContent(content);
        pageScroll.setFitToWidth(true);
        pageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pageScroll.getStyleClass().add("page-scroll");

        NavBar navBar = new NavBar(categories, () -> scrollTo(0), this::scrollToCategory);
        setTop(navBar);
        setCenter(pageScroll);
    }

    private void scrollToCategory(Category category) {
        CategoryRow row = rows.get(category);
        if (row != null) {
            scrollTo(row.getBoundsInParent().getMinY() - 12);
        }
    }

    // Smoothly moves the page to the selected category or back to the top.
    private void scrollTo(double y) {
        double maxPx = content.getHeight() - pageScroll.getViewportBounds().getHeight();
        if (maxPx <= 0) {
            return;
        }
        double target = Math.max(0, Math.min(1, y / maxPx));
        new Timeline(new KeyFrame(Duration.millis(500),
                new KeyValue(pageScroll.vvalueProperty(), target, Interpolator.EASE_BOTH))).play();
    }
}
