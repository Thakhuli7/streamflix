package com.streamflix.ui;

import com.streamflix.audio.SoundManager;
import com.streamflix.model.Category;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/*
 * Displays the app logo, category navigation links, and sound control.
 */
public class NavBar extends HBox {

    private final List<CustomButton> links = new ArrayList<>();
    private final CustomButton soundButton;

    public NavBar(List<Category> categories, Runnable onHome, Consumer<Category> onCategory) {
        super(10);
        getStyleClass().add("nav-bar");
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(0, 36, 0, 36));

        Label logo = new Label("STREAMFLIX");
        logo.getStyleClass().add("logo");
        HBox.setMargin(logo, new Insets(0, 24, 0, 0));
        getChildren().add(logo);

        CustomButton homeLink = createLink("Home", onHome);
        getChildren().add(homeLink);
        homeLink.setActive(true);

        for (Category category : categories) {
            getChildren().add(createLink(category.getName(), () -> onCategory.accept(category)));
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().add(spacer);

        soundButton = new CustomButton(null, IconFactory.speaker(true, 20, Color.WHITE),
                CustomButton.Style.ICON, this::toggleSound);
        soundButton.setPrefSize(40, 40);
        getChildren().add(soundButton);
    }

    private CustomButton createLink(String text, Runnable onClick) {
        CustomButton[] holder = new CustomButton[1];
        holder[0] = new CustomButton(text, null, CustomButton.Style.LINK, () -> {
            links.forEach(l -> l.setActive(false));
            holder[0].setActive(true);
            onClick.run();
        });
        links.add(holder[0]);
        return holder[0];
    }

    private void toggleSound() {
        boolean muted = SoundManager.getInstance().toggleMuted();
        soundButton.setIcon(IconFactory.speaker(!muted, 20, Color.WHITE));
    }
}
