package com.streamflix.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/*
 * Displays rotating rings and a pulsing logo while the trailer loads.
 */
public class LoadingAnimation extends StackPane {

    private final List<Animation> animations = new ArrayList<>();
    private final Label caption = new Label("Loading trailer");
    private final Label movieLabel = new Label();
    private final Timeline dotsTimeline;
    private FadeTransition fade;
    private int dotCount;

    public LoadingAnimation() {
        getStyleClass().add("loading-overlay");

        Pane outer = createRing(70, 5, Color.web("#E50914"), 110, 1.3, false);
        Pane middle = createRing(50, 4, Color.WHITE, 70, 1.8, true);
        Pane inner = createRing(30, 3, Color.web("#E50914"), 90, 1.0, false);

        Text logo = new Text("S");
        logo.getStyleClass().add("loading-logo");
        ScaleTransition pulse = new ScaleTransition(Duration.millis(700), logo);
        pulse.setFromX(0.85);
        pulse.setFromY(0.85);
        pulse.setToX(1.2);
        pulse.setToY(1.2);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setInterpolator(Interpolator.EASE_BOTH);
        animations.add(pulse);

        StackPane spinner = new StackPane(outer, middle, inner, logo);
        caption.getStyleClass().add("loading-caption");
        movieLabel.getStyleClass().add("loading-movie");

        VBox box = new VBox(20, spinner, caption, movieLabel);
        box.setAlignment(Pos.CENTER);
        box.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        getChildren().add(box);

        dotsTimeline = new Timeline(new KeyFrame(Duration.millis(400), e -> {
            dotCount = (dotCount + 1) % 4;
            caption.setText("Loading trailer" + ".".repeat(dotCount));
        }));
        dotsTimeline.setCycleCount(Animation.INDEFINITE);
    }
    private Pane createRing(double radius, double strokeWidth, Color color,
                            double arcLength, double seconds, boolean reverse) {
        double size = 2 * radius + 2 * strokeWidth;
        Arc arc = new Arc(size / 2, size / 2, radius, radius, 0, arcLength);
        arc.setType(ArcType.OPEN);
        arc.setFill(null);
        arc.setStroke(color);
        arc.setStrokeWidth(strokeWidth);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);

        Pane pane = new Pane(arc);
        pane.setMinSize(size, size);
        pane.setPrefSize(size, size);
        pane.setMaxSize(size, size);

        RotateTransition rotation = new RotateTransition(Duration.seconds(seconds), pane);
        rotation.setByAngle(reverse ? -360 : 360);
        rotation.setCycleCount(Animation.INDEFINITE);
        rotation.setInterpolator(Interpolator.LINEAR);
        animations.add(rotation);
        return pane;
    }

    public void start(String movieTitle) {
        if (fade != null) {
            fade.stop();
        }
        movieLabel.setText(movieTitle);
        setOpacity(1);
        setVisible(true);
        animations.forEach(Animation::play);
        dotsTimeline.play();
    }

    public void dismiss() {
        if (!isVisible() || (fade != null && fade.getStatus() == Animation.Status.RUNNING)) {
            return; 
        }
        fade = new FadeTransition(Duration.millis(450), this);
        fade.setToValue(0);
        fade.setOnFinished(e -> stopNow());
        fade.play();
    }

    public void stopNow() {
        if (fade != null) {
            fade.stop();
        }
        setVisible(false);
        animations.forEach(Animation::stop);
        dotsTimeline.stop();
    }
}
