package com.streamflix.ui;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/*
 * Draws vector icons so the app does not need external icon image files.
 */
public final class IconFactory {

    private IconFactory() {
    }

    public static Node play(double size, Color color) {
        Polygon triangle = new Polygon(0, 0, 0, size, size * 0.92, size / 2);
        triangle.setFill(color);
        return triangle;
    }

    public static Node pause(double size, Color color) {
        Rectangle left = new Rectangle(size * 0.3, size);
        Rectangle right = new Rectangle(size * 0.3, size);
        left.setFill(color);
        right.setFill(color);
        return new HBox(size * 0.26, left, right);
    }

    // Creates the left or right chevron used in category row controls.
    public static Node chevron(boolean pointRight, double size, Color color) {
        Polyline line = pointRight
                ? new Polyline(0, 0, size * 0.55, size / 2, 0, size)
                : new Polyline(size * 0.55, 0, 0, size / 2, size * 0.55, size);
        styleStroke(line, size * 0.14, color);
        return line;
    }

    public static Node backArrow(double size, Color color) {
        Line shaft = new Line(size * 0.1, size / 2, size, size / 2);
        Polyline head = new Polyline(size * 0.5, size * 0.1, size * 0.1, size / 2, size * 0.5, size * 0.9);
        styleStroke(shaft, size * 0.12, color);
        styleStroke(head, size * 0.12, color);
        return new Group(shaft, head);
    }

    // Creates the speaker icon and draws a crossed-out symbol when muted.
    public static Node speaker(boolean soundOn, double size, Color color) {
        Polygon body = new Polygon(
                0, size * 0.35, size * 0.28, size * 0.35, size * 0.58, size * 0.1,
                size * 0.58, size * 0.9, size * 0.28, size * 0.65, 0, size * 0.65);
        body.setFill(color);
        Group icon = new Group(body);
        if (soundOn) {
            for (int i = 1; i <= 2; i++) {
                double r = size * 0.18 * i + size * 0.1;
                Arc wave = new Arc(size * 0.58, size / 2, r, r, -45, 90);
                wave.setType(ArcType.OPEN);
                styleStroke(wave, size * 0.09, color);
                icon.getChildren().add(wave);
            }
        } else {
            Line a = new Line(size * 0.72, size * 0.32, size * 1.02, size * 0.68);
            Line b = new Line(size * 1.02, size * 0.32, size * 0.72, size * 0.68);
            styleStroke(a, size * 0.1, color);
            styleStroke(b, size * 0.1, color);
            icon.getChildren().addAll(a, b);
        }
        return icon;
    }

    // Creates the four-corner icon used for fullscreen.
    public static Node expand(double size, Color color) {
        double c = size * 0.35;
        Polyline tl = new Polyline(0, c, 0, 0, c, 0);
        Polyline tr = new Polyline(size - c, 0, size, 0, size, c);
        Polyline br = new Polyline(size, size - c, size, size, size - c, size);
        Polyline bl = new Polyline(c, size, 0, size, 0, size - c);
        for (Polyline p : new Polyline[]{tl, tr, br, bl}) {
            styleStroke(p, size * 0.11, color);
        }
        return new Group(tl, tr, br, bl);
    }

    private static void styleStroke(javafx.scene.shape.Shape shape, double width, Color color) {
        shape.setFill(null);
        shape.setStroke(color);
        shape.setStrokeWidth(width);
        shape.setStrokeLineCap(StrokeLineCap.ROUND);
        shape.setStrokeLineJoin(StrokeLineJoin.ROUND);
    }
}
