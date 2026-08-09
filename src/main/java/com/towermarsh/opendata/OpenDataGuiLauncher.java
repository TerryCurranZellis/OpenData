package com.towermarsh.opendata;

import com.towermarsh.opendata.gui.OpenDataGuiApplication;

/**
 * Temporary standalone launcher for the GUI sample.
 *
 * <p>This class is deliberately outside the GUI package. It demonstrates how
 * the existing OpenData main program can later launch the graphical interface,
 * without integrating that existing main program yet.</p>
 */
public final class OpenDataGuiLauncher {

    private OpenDataGuiLauncher() {
        // Utility class.
    }

    public static void main(final String[] args) {
        OpenDataGuiApplication.launchGui(args);
    }
}
