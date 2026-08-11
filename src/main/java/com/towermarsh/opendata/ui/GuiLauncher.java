package com.towermarsh.opendata;

import com.towermarsh.opendata.gui.OpenDataGuiApplication;

/**
 * Standalone launcher for the GUI sample.
 *
 * <p>This class is deliberately outside the GUI package. It demonstrates how
 * the existing OpenData main program can later launch the graphical interface,
 * without integrating that existing main program yet.</p>
 */
public final class GuiLauncher {

    private GuiLauncher() {
        // Utility class.
    }

    public static void launch(final String[] args) {
        OpenDataGuiApplication.launchGui(args);
    }
}
