/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ui;

/**
 * Compatibility launcher retained while callers move to the JavaFX GUI package.
 *
 * @author Terry Curran
 * @version 3.1.0
 * @deprecated use {@link com.towermarsh.opendata.gui.GuiLauncher}; the GUI
 * launcher now belongs to the JavaFX package tree.
 */
@Deprecated(since = "3.1.0")
public final class GuiLauncher {

    private GuiLauncher() {
        // Utility class.
    }

    /**
     * Starts the JavaFX interface.
     *
     * @param args arguments passed to JavaFX
     * @deprecated use {@link com.towermarsh.opendata.gui.GuiLauncher#launch(String...)}
     */
    @Deprecated(since = "3.1.0")
    public static void launch(final String... args) {
        com.towermarsh.opendata.gui.GuiLauncher.launch(args);
    }
}
