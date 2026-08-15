/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Validated, not-yet-registered plugin definition discovered by the GUI.
 *
 * @param pluginId plugin identifier read from the properties file
 * @param displayName plugin display name
 * @param file source properties file
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public record PluginRegistrationCandidate(
        String pluginId,
        String displayName,
        Path file) {

    /**
     * Validates and normalises the candidate.
     */
    public PluginRegistrationCandidate {
        pluginId = Objects.requireNonNull(pluginId, "pluginId").trim();
        displayName = Objects.requireNonNull(displayName, "displayName").trim();
        file = Objects.requireNonNull(file, "file").toAbsolutePath().normalize();
        if (pluginId.isEmpty()) {
            throw new IllegalArgumentException("pluginId must not be blank.");
        }
        if (displayName.isEmpty()) {
            throw new IllegalArgumentException("displayName must not be blank.");
        }
    }
}
