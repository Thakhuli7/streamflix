package com.streamflix.ui;

import com.streamflix.audio.SoundManager;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/*
 * A custom JavaFX control made from labels and icons, not the Button class.
 */
public class CustomButton extends StackPane {

    // Styles available for navigation, playback, and other app actions.
    public enum Style { PRIMARY, SECONDARY, ICON, LINK }

    private static final double HOVER_SCALE = 1.07;
    private static final double PRESS_SCALE = 0.95;

    private final HBox content = new HBox(8);
    private final Label label;
    private final Runnable action;
    private boolean hasIcon;

    public CustomButton(String text, Node icon, Style style, Runnable action) {
        this.action = action;

        getStyleClass().addAll("custom-button", "cb-" + style.name().toLowerCase());
        content.setAlignment(Pos.CENTER);
        content.setMouseTransparent(true);

        if (icon != null) {
            content.getChildren().add(icon);
            hasIcon = true;
        }
        if (text != null) {
            label = new Label(text);
            label.getStyleClass().add("cb-label");
            label.setMouseTransparent(true);
            content.getChildren().add(label);
        } else {
            label = null;
        }
        getChildren().add(content);

        // Keep the control the size of its text and icon.
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setCursor(Cursor.HAND);
        setFocusTraversable(true);
        installInteractions();
    }

    // Replaces the icon, for example when play changes to pause.
    public void setIcon(Node icon) {
        if (hasIcon) {
            content.getChildren().set(0, icon);
        } else {
            content.getChildren().add(0, icon);
            hasIcon = true;
        }
    }

    public void setText(String text) {
        if (label != null) {
            label.setText(text);
        }
    }

    // Marks a navigation link as selected.
    public void setActive(boolean active) {
        if (active) {
            if (!getStyleClass().contains("active")) {
                getStyleClass().add("active");
            }
        } else {
            getStyleClass().remove("active");
        }
    }

    private void installInteractions() {
        setOnMouseEntered(e -> animateScale(HOVER_SCALE));
        setOnMouseExited(e -> animateScale(1.0));
        setOnMousePressed(e -> animateScale(PRESS_SCALE));
        setOnMouseReleased(e -> animateScale(isHover() ? HOVER_SCALE : 1.0));
        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                trigger();
            }
        });
        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                trigger();
                e.consume();
            }
        });
    }

    private void trigger() {
        SoundManager.getInstance().play(SoundManager.Effect.CLICK);
        if (action != null) {
            action.run();
        }
    }

    private void animateScale(double target) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(120), this);
        transition.setToX(target);
        transition.setToY(target);
        transition.play();
    }
}
