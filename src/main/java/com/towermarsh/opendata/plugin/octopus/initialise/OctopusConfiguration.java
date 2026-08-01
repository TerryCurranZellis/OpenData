/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.initialise;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyDefinition;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Typed configuration for the Octopus plugin.
 *
 * <p>Holds the paths and settings resolved from the plugin property definitions
 * at startup. This class is populated by {@link OctopusInitialise} before the
 * ETL pipeline begins.
 *
 * @param inputDirectory   directory containing Octopus Energy statement PDF files
 *                         named {@code octopus-energy-statement-YYYY-MM-DD.pdf}
 * @param workingDirectory temporary working directory used during processing;
 *                         created if absent
 * @param archiveDirectory directory to which processed PDF files are moved after
 *                         a successful write run
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public record OctopusConfiguration(
        Path inputDirectory,
        Path workingDirectory,
        Path archiveDirectory) {

    /** Property key for the PDF input directory. */
    public static final String PROP_INPUT_DIRECTORY = "input.directory";
    /** Property key for the working/temp directory. */
    public static final String PROP_WORKING_DIRECTORY = "working.directory";
    /** Property key for the archive directory. */
    public static final String PROP_ARCHIVE_DIRECTORY = "archive.directory";

    /** Validates and normalises record components. */
    public OctopusConfiguration {
        Objects.requireNonNull(inputDirectory, "inputDirectory");
        Objects.requireNonNull(workingDirectory, "workingDirectory");
        Objects.requireNonNull(archiveDirectory, "archiveDirectory");
    }

    /**
     * Builds typed Octopus configuration from a resolved plugin definition.
     *
     * @param definition resolved plugin definition
     * @return typed Octopus configuration
     */
    public static OctopusConfiguration from(final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");

        return new OctopusConfiguration(
                requirePath(definition, PROP_INPUT_DIRECTORY),
                requirePath(definition, PROP_WORKING_DIRECTORY),
                requirePath(definition, PROP_ARCHIVE_DIRECTORY));
    }

    private static Path requirePath(final PluginDefinition definition, final String key) {
        return definition.findProperty(key)
                .map(PluginPropertyDefinition::value)
                .map(Path::of)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plugin '%s' requires property '%s'.".formatted(definition.id(), key)));
    }
}
