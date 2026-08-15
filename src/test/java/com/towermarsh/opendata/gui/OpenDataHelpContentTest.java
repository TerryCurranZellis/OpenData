/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Tests availability of the built-in JavaFX fallback help text.
 *
 * @author Terry Curran
 * @version 3.0.0
 */
class OpenDataHelpContentTest {

    @Test
    void packagedHelpDescribesCoreGuiActions() {
        final var help = OpenDataHelpContent.load();

        assertTrue(help.contains("OpenData Processing Framework"));
        assertTrue(help.contains("Plugin Detail"));
        assertTrue(help.contains("Logs"));
    }
}
