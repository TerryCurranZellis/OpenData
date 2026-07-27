/*
 * Filename: CommandLineArguments.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.cli;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Immutable command-line arguments for one invocation.
 *
 * @param pluginIds selected plugin identifiers
 * @param allPluginsRequested whether all installed plugins were requested
 * @param overrideFile optional external override properties file
 * @param parallelism optional plugin parallelism override
 * @param dryRun whether database writes and audit rows are disabled
 * @param verbose whether verbose logging is requested
 * @param helpRequested whether help output was requested
 * @param versionRequested whether version output was requested
 * @param listPluginsRequested whether installed plugin listing was requested
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record CommandLineArguments(
        List<String> pluginIds,
        boolean allPluginsRequested,
        Optional<Path> overrideFile,
        OptionalInt parallelism,
        boolean dryRun,
        boolean verbose,
        boolean helpRequested,
        boolean versionRequested,
        boolean listPluginsRequested) {

    /** Validates record components. */
    /** Validates and normalises record components. */
    public CommandLineArguments {
        pluginIds = List.copyOf(Objects.requireNonNull(pluginIds, "pluginIds"));
        overrideFile = overrideFile == null ? Optional.empty() : overrideFile;
        parallelism = parallelism == null ? OptionalInt.empty() : parallelism;
        if (allPluginsRequested && !pluginIds.isEmpty()) {
            throw new IllegalArgumentException("'all' cannot be combined with named plugins.");
        }
        parallelism.ifPresent(value -> {
            if (value < 1 || value > 64) {
                throw new IllegalArgumentException("parallelism must be between 1 and 64.");
            }
        });
    }

    /**
     * Indicates whether the invocation only requests informational output.
     *
     * @return {@code true} when help, version, or plugin listing output was requested
     */
    public boolean informationalRequest() {
        return helpRequested || versionRequested || listPluginsRequested;
    }
}
