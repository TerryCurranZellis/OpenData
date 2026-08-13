/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import java.util.Objects;

/**
 * Launches the OpenData JavaFX desktop application.
 *
 * <p>The launcher provides a small boundary between the application entry point
 * and JavaFX. {@link javafx.application.Application#launch(Class, String...)}
 * does not return until the JavaFX application has stopped, so resources owned
 * by {@code OpenData.main} remain available for the complete lifetime of the
 * graphical interface.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class GuiLauncher {

    private GuiLauncher() {
        // Utility class.
    }

    /**
     * Starts the JavaFX interface and blocks until the interface is closed.
     *
     * @param args arguments to expose to the JavaFX application
     */
    public static void launch(final String... args) {
        OpenDataGuiApplication.launchGui(Objects.requireNonNull(args, "args"));
    }
}
