package com.streamflix;

import com.streamflix.ui.AppRoot;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/*
 * Starts the JavaFX window and loads the StreamFlix stylesheet.
 */
public class StreamFlixApp extends Application {

    @Override
    public void start(Stage stage) {
        AppRoot root = new AppRoot(getHostServices(), stage);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double windowWidth = Math.min(1366, screenBounds.getWidth() - 40);
        double windowHeight = Math.min(820, screenBounds.getHeight() - 40);
        Scene scene = new Scene(root, windowWidth, windowHeight);
        scene.getStylesheets().add(getClass().getResource("/css/streamflix.css").toExternalForm());

        stage.setTitle("StreamFlix - Movie Streaming Demo");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.setX((screenBounds.getWidth() - windowWidth) / 2);
        stage.setY((screenBounds.getHeight() - windowHeight) / 2);
        stage.show();
    }
}
