/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX application for the OpenData graphical interface.
 *
 * <p>This class owns JavaFX startup, the JavaFX splash stage and FXML loading.
 * It deliberately contains no OpenData command-line or processing-framework
 * startup logic.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public final class OpenDataGuiApplication extends Application {

    private static final String VIEW_RESOURCE = "OpenDataMainView.fxml";
    private static final String APPLICATION_ICON = "/opendata-icon-32.png";

    private static final double INITIAL_WIDTH = 1280.0;
    private static final double INITIAL_HEIGHT = 800.0;
    private static final double MIN_WIDTH = 960.0;
    private static final double MIN_HEIGHT = 620.0;

    /**
     * Starts the JavaFX application with the required startup splash.
     *
     * <p>The splash appears first. The main window is prepared while the splash
     * is visible and is shown only after the splash has been visible for at
     * least five seconds.</p>
     *
     * @param stage primary application stage
     * @throws IOException if the FXML cannot be loaded
     */
    @Override
    public void start(final Stage stage) throws IOException {
        final var splash = OpenDataSplashScreen.show();
        try {
            configureMainStage(stage);
            splash.closeAfterMinimumDisplay(stage::show);
        } catch (IOException | RuntimeException exception) {
            splash.closeNow();
            throw exception;
        }
    }

    /**
     * Loads and configures the main JavaFX stage without displaying it.
     *
     * @param stage primary application stage
     * @throws IOException if the FXML cannot be loaded
     */
    private static void configureMainStage(final Stage stage) throws IOException {
        final var view = Objects.requireNonNull(
                OpenDataGuiApplication.class.getResource(VIEW_RESOURCE),
                "Unable to locate " + VIEW_RESOURCE);

        final var loader = new FXMLLoader(view);
        final Parent root = loader.load();
        final var scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);

        final var iconResource = OpenDataGuiApplication.class.getResourceAsStream(APPLICATION_ICON);
        if (iconResource != null) {
            try (iconResource) {
                stage.getIcons().add(new Image(iconResource));
            }
        }

        stage.setTitle("OpenData Processing Framework");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    /**
     * Starts the JavaFX GUI and returns only after the JavaFX application has
     * stopped.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void launchGui(final String... args) {
        Application.launch(OpenDataGuiApplication.class, args);
    }
}
