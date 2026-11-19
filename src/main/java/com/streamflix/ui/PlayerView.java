package com.streamflix.ui;

import com.streamflix.model.Movie;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.HostServices;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
 * Loads YouTube trailers inside the app and provides custom playback controls.
 */
public class PlayerView extends StackPane {

    // A desktop browser user agent helps YouTube serve its embedded player.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                    + "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

    // JavaScript used to check whether the video is loading, playing, or paused.
    private static final String SCRIPT_STATE =
            "(function(){var v=document.querySelector('video');"
                    + "if(!v){return 'none';}"
                    + "if(v.readyState<3){return 'loading';}"
                    + "return v.paused?'paused':'playing';})()";

    private static final String SCRIPT_TOGGLE =
            "(function(){var v=document.querySelector('video');"
                    + "if(v){if(v.paused){v.play();}else{v.pause();}}})()";

    private static final String SCRIPT_PLAY =
            "(function(){var v=document.querySelector('video');if(v){v.play();}})()";

    // Hide YouTube interface elements that the app does not need.
    private static final String SCRIPT_HIDE_CHROME =
            "(function(){var s=document.createElement('style');"
                    + "s.innerHTML='.ytp-chrome-top,.ytp-gradient-top,.ytp-watermark,"
                    + ".ytp-pause-overlay,.ytp-show-cards-title,.ytp-ce-element{display:none!important}';"
                    + "document.head.appendChild(s);})()";

    // A 400 ms timer is checked 40 times, allowing up to about 16 seconds for loading.
    private static final int TIMEOUT_TICKS = 40;

    private final HostServices hostServices;
    private final Stage stage;

    private final WebView webView = new WebView();
    private final WebEngine engine = webView.getEngine();
    private final LoadingAnimation loading = new LoadingAnimation();
    private final Label titleLabel = new Label();
    private final Label statusLabel = new Label("Paused");
    private final CustomButton playPauseButton;
    private final HBox topBar;
    private final HBox bottomBar;
    private final VBox errorBox = new VBox(14);
    private final Label errorText = new Label();

    private final Timeline poller;
    private final PauseTransition idleTimer = new PauseTransition(Duration.seconds(3));

    private Movie current;
    private boolean playing;
    private Boolean shownState;
    private boolean controlsVisible = true;
    private boolean playbackStarted;
    private boolean autoplayTried;
    private int stalledTicks;

    public PlayerView(HostServices hostServices, Stage stage, Runnable onBack) {
        this.hostServices = hostServices;
        this.stage = stage;
        getStyleClass().add("player-view");

        engine.setUserAgent(USER_AGENT);

        
        CustomButton backButton = new CustomButton("Back",
                IconFactory.backArrow(16, Color.WHITE), CustomButton.Style.SECONDARY, onBack);
        titleLabel.getStyleClass().add("player-title");
        topBar = new HBox(20, backButton, titleLabel);
        topBar.getStyleClass().add("player-top");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setMaxHeight(USE_PREF_SIZE);
        StackPane.setAlignment(topBar, Pos.TOP_CENTER);

        
        CustomButton rewind = new CustomButton("-10s", null, CustomButton.Style.ICON, () -> seek(-10));
        CustomButton forward = new CustomButton("+10s", null, CustomButton.Style.ICON, () -> seek(10));
        playPauseButton = new CustomButton(null, IconFactory.play(22, Color.WHITE),
                CustomButton.Style.ICON, this::togglePlayback);
        rewind.setPrefSize(50, 50);
        forward.setPrefSize(50, 50);
        playPauseButton.setPrefSize(62, 62);

        statusLabel.getStyleClass().add("player-status");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        CustomButton fullscreen = new CustomButton(null, IconFactory.expand(20, Color.WHITE),
                CustomButton.Style.ICON, this::toggleFullscreen);
        fullscreen.setPrefSize(50, 50);

        bottomBar = new HBox(18, rewind, playPauseButton, forward, statusLabel, spacer, fullscreen);
        bottomBar.getStyleClass().add("player-bottom");
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setMaxHeight(USE_PREF_SIZE);
        StackPane.setAlignment(bottomBar, Pos.BOTTOM_CENTER);

        
        Label errorTitle = new Label("Trailer unavailable");
        errorTitle.getStyleClass().add("error-title");
        errorText.getStyleClass().add("error-text");
        errorText.setWrapText(true);
        errorText.setMaxWidth(420);
        errorText.setAlignment(Pos.CENTER);
        CustomButton watchOnYoutube = new CustomButton("Watch on YouTube", null, CustomButton.Style.PRIMARY,
                () -> {
                    if (current != null) {
                        hostServices.showDocument(current.getWatchUrl());
                    }
                });
        CustomButton goBack = new CustomButton("Go back", null, CustomButton.Style.SECONDARY, onBack);
        HBox errorButtons = new HBox(12, watchOnYoutube, goBack);
        errorButtons.setAlignment(Pos.CENTER);
        errorBox.getChildren().addAll(errorTitle, errorText, errorButtons);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        errorBox.getStyleClass().add("error-box");
        errorBox.setVisible(false);

        loading.setVisible(false);
        getChildren().addAll(webView, loading, errorBox, topBar, bottomBar);
        setPadding(Insets.EMPTY);

        
        poller = new Timeline(new KeyFrame(Duration.millis(400), e -> poll()));
        poller.setCycleCount(Animation.INDEFINITE);

        engine.getLoadWorker().stateProperty().addListener((obs, old, state) -> {
            if (current == null) {
                return; 
            }
            if (state == Worker.State.SUCCEEDED) {
                run(SCRIPT_HIDE_CHROME);
                poller.play();
            } else if (state == Worker.State.FAILED) {
                showError("The trailer could not be loaded. Check your internet connection.");
            }
        });

        
        addEventFilter(MouseEvent.MOUSE_MOVED, e -> wakeControls());
        idleTimer.setOnFinished(e -> {
            if (playing) {
                setControlsVisible(false);
            }
        });
    }

    

    
    public void open(Movie movie) {
        current = movie;
        playbackStarted = false;
        autoplayTried = false;
        stalledTicks = 0;
        shownState = null;
        errorBox.setVisible(false);
        titleLabel.setText(movie.getTitle() + "  (" + movie.getYear() + ")");
        updatePlaying(false);
        setControlsVisible(true);
        loading.start(movie.getTitle());
        poller.stop();
        engine.load(movie.getEmbedUrl());
    }

    
    public void close() {
        current = null;
        poller.stop();
        idleTimer.stop();
        loading.stopNow();
        engine.load("about:blank");
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        }
    }

    

    private void togglePlayback() {
        run(SCRIPT_TOGGLE);
        poll(); 
    }

    private void seek(int seconds) {
        run("(function(){var v=document.querySelector('video');"
                + "if(v){v.currentTime=Math.max(0,v.currentTime+(" + seconds + "));}})()");
    }

    private void toggleFullscreen() {
        stage.setFullScreenExitHint("");
        stage.setFullScreen(!stage.isFullScreen());
    }

    

    
    private void poll() {
        if (current == null) {
            return;
        }
        Object result = run(SCRIPT_STATE);
        String state = result == null ? "none" : result.toString();

        switch (state) {
            case "playing":
                playbackStarted = true;
                updatePlaying(true);
                loading.dismiss();
                break;
            case "paused":
                if (!playbackStarted && !autoplayTried) {
                    autoplayTried = true;     
                    run(SCRIPT_PLAY);
                } else if (playbackStarted) {
                    updatePlaying(false);     
                }
                break;
            default:
                break;                        
        }

        if (!playbackStarted && ++stalledTicks > TIMEOUT_TICKS) {
            showError("YouTube did not start the trailer. It may be unavailable here, "
                    + "or the connection is slow.");
        }
    }

    private void updatePlaying(boolean nowPlaying) {
        if (shownState != null && shownState == nowPlaying) {
            return; 
        }
        shownState = nowPlaying;
        playing = nowPlaying;
        playPauseButton.setIcon(nowPlaying ? IconFactory.pause(22, Color.WHITE)
                                           : IconFactory.play(22, Color.WHITE));
        statusLabel.setText(nowPlaying ? "Playing" : "Paused");
        if (nowPlaying) {
            idleTimer.playFromStart();
        } else {
            setControlsVisible(true);
        }
    }

    private void showError(String message) {
        poller.stop();
        loading.stopNow();
        errorText.setText(message);
        errorBox.setVisible(true);
        setControlsVisible(true);
    }

    

    private void wakeControls() {
        setControlsVisible(true);
        idleTimer.playFromStart();
    }

    private void setControlsVisible(boolean visible) {
        if (controlsVisible == visible) {
            return;
        }
        controlsVisible = visible;
        for (HBox bar : new HBox[]{topBar, bottomBar}) {
            bar.setMouseTransparent(!visible);
            FadeTransition fade = new FadeTransition(Duration.millis(250), bar);
            fade.setToValue(visible ? 1 : 0);
            fade.play();
        }
    }

    
    private Object run(String script) {
        try {
            return engine.executeScript(script);
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
