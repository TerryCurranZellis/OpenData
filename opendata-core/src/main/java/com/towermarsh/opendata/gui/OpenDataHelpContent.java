/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Loads the built-in JavaFX help overview used until compiled Windows Help is
 * integrated in the final GUI batch.
 *
 * @author Terry Curran
 * @version 3.0.0
 */
final class OpenDataHelpContent {

    private static final String RESOURCE = "OpenDataHelp.txt";

    private OpenDataHelpContent() {
    }

    static String load() {
        try (InputStream input = OpenDataHelpContent.class.getResourceAsStream(RESOURCE)) {
            if (input == null) {
                return "OpenData help content is not available.";
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return "OpenData help content could not be loaded: " + exception.getMessage();
        }
    }
}
