/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ui;

import java.util.logging.Logger;

/**
 * Launches the OpenData graphical user interface.
 *
 * <p>
 * This implementation is a temporary placeholder. It will be replaced by
 * the JavaFX GUI implementation when the Version 3 GUI branch is merged.
 * </p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public final class GuiLauncher {

    private static final Logger LOGGER =
            Logger.getLogger(GuiLauncher.class.getName());

    /**
     * Prevents instantiation.
     */
    private GuiLauncher() {
    }

    /**
     * Starts the graphical user interface.
     *
     * @param arguments original application arguments
     */
    public static void launch(final String[] arguments) {
        LOGGER.info(
                "OpenData GUI requested; "
                + "the graphical interface is not yet implemented.");
    }
}