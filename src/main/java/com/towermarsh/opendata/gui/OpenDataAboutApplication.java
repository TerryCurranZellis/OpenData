/*
 * Copyright Â© 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.app.ApplicationInfo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Minimal JavaFX application used by the command-line {@code --about} route.
 *
 * <p>This removes the active dependency on the deprecated Swing About dialog
 * while keeping {@code --about} as a standalone command.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public final class OpenDataAboutApplication extends Application {

    /** 
     * Displays the JavaFX About dialog and terminates its JavaFX session.
     * @param primaryStage */
    @Override
    public void start(final Stage primaryStage) {
        OpenDataInformationDialogs.showAbout(null, ApplicationInfo.current());
        Platform.exit();
    }

    /**
     * Starts the standalone JavaFX About command.
     */
    public static void launchAbout() {
        Application.launch(OpenDataAboutApplication.class);
    }
}
