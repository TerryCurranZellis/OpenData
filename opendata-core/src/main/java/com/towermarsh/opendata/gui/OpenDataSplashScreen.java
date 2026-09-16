/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import java.util.Objects;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * JavaFX startup splash used by the OpenData desktop interface.
 *
 * <p>The splash is intentionally a small undecorated JavaFX stage rather than a
 * Swing window. It remains visible for at least five seconds and is then closed
 * immediately before the main application stage is shown.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
final class OpenDataSplashScreen {

    /** Minimum time for which the splash remains visible. */
    static final Duration MINIMUM_DISPLAY_DURATION = Duration.seconds(5);

    private static final String SPLASH_RESOURCE
            = "/com/towermarsh/opendata/gui/opendata-splash.png";
    private static final double MAXIMUM_IMAGE_WIDTH = 600.0;

    private final Stage stage;
    private final long shownAtNanos;

    private OpenDataSplashScreen(final Stage stage, final long shownAtNanos) {
        this.stage = stage;
        this.shownAtNanos = shownAtNanos;
    }

    /**
     * Creates, centres and displays the splash stage.
     *
     * @return the displayed splash instance
     */
    static OpenDataSplashScreen show() {
        final var resource = Objects.requireNonNull(
                OpenDataSplashScreen.class.getResourceAsStream(SPLASH_RESOURCE),
                "Unable to locate splash resource " + SPLASH_RESOURCE);

        final Image image;
        try (resource) {
            image = new Image(resource, MAXIMUM_IMAGE_WIDTH, 0.0, true, true);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Unable to close splash resource.", exception);
        }

        final var imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        final var root = new StackPane(imageView);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #021639;"
                + "-fx-border-color: #23bcee;"
                + "-fx-border-width: 1;");

        final var splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.setAlwaysOnTop(true);
        splashStage.setScene(new Scene(root));
        splashStage.sizeToScene();
        splashStage.centerOnScreen();
        splashStage.show();

        return new OpenDataSplashScreen(splashStage, System.nanoTime());
    }

    /**
     * Runs the supplied main-window action after the five-second minimum and
     * immediately closes the splash. The splash is always-on-top, so the user
     * sees a direct transition without a zero-window gap.
     *
     * @param afterClose action to run after the splash closes
     */
    void closeAfterMinimumDisplay(final Runnable afterClose) {
        Objects.requireNonNull(afterClose, "afterClose");

        final long minimumNanos = (long) (MINIMUM_DISPLAY_DURATION.toMillis() * 1_000_000L);
        final long elapsedNanos = Math.max(0L, System.nanoTime() - shownAtNanos);
        final long remainingNanos = Math.max(0L, minimumNanos - elapsedNanos);
        final double remainingMillis = remainingNanos / 1_000_000.0;

        final var pause = new PauseTransition(Duration.millis(remainingMillis));
        pause.setOnFinished(event -> {
            afterClose.run();
            closeNow();
        });
        pause.play();
    }

    /** Immediately closes the splash, used if main-window preparation fails. */
    void closeNow() {
        stage.hide();
        stage.close();
    }
}
