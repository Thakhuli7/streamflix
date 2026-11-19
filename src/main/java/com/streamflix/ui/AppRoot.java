package com.streamflix.ui;

import com.streamflix.audio.SoundManager;
import com.streamflix.model.Movie;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.HostServices;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
 * Switches between the home screen and the trailer player.
 */
public class AppRoot extends StackPane {

    private final HomeView homeView;
    private final PlayerView playerView;

    public AppRoot(HostServices hostServices, Stage stage) {
        getStyleClass().add("app-root");

        homeView = new HomeView(this::openPlayer);
        playerView = new PlayerView(hostServices, stage, this::closePlayer);
        playerView.setVisible(false);
        playerView.setOpacity(0);

        getChildren().addAll(homeView, playerView);
        addSplash();

        // Escape closes the player when the window is not in fullscreen mode.
        addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE && playerView.isVisible() && !stage.isFullScreen()) {
                closePlayer();
                e.consume();
            }
        });
    }

    private void openPlayer(Movie movie) {
        if (playerView.isVisible()) {
            return;
        }
        SoundManager.getInstance().play(SoundManager.Effect.OPEN);
        playerView.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(350), playerView);
        fadeIn.setToValue(1);
        fadeIn.play();
        playerView.open(movie);
    }

    private void closePlayer() {
        if (!playerView.isVisible()) {
            return;
        }
        SoundManager.getInstance().play(SoundManager.Effect.BACK);
        playerView.close();
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), playerView);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> playerView.setVisible(false));
        fadeOut.play();
    }

    // Displays the short StreamFlix animated introduction.
    private void addSplash() {
        StackPane splash = new StackPane();
        splash.getStyleClass().add("splash");
        Label logo = new Label("STREAMFLIX");
        logo.getStyleClass().add("splash-logo");
        splash.getChildren().add(logo);
        getChildren().add(splash);

        logo.setOpacity(0);
        logo.setScaleX(0.7);
        logo.setScaleY(0.7);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(700), logo);
        fadeIn.setToValue(1);
        ScaleTransition grow = new ScaleTransition(Duration.millis(1300), logo);
        grow.setToX(1.15);
        grow.setToY(1.15);
        PauseTransition hold = new PauseTransition(Duration.millis(300));
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), splash);
        fadeOut.setToValue(0);

        SequentialTransition intro = new SequentialTransition(new ParallelTransition(fadeIn, grow), hold, fadeOut);
        intro.setOnFinished(e -> getChildren().remove(splash));
        SoundManager.getInstance().play(SoundManager.Effect.OPEN);
        intro.play();
    }
}
