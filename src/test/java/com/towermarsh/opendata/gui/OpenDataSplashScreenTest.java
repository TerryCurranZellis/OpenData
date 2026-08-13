/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Tests the startup splash policy without starting the JavaFX toolkit.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
class OpenDataSplashScreenTest {

    @Test
    void minimumDisplayDurationIsFiveSeconds() {
        assertEquals(5_000.0,
                OpenDataSplashScreen.MINIMUM_DISPLAY_DURATION.toMillis(),
                0.001);
    }
}
