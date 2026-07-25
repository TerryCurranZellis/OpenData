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
 * @param pluginIds List of plugins
 * @param allPluginsRequested trues if all plugins to be processed else false
 * @param overrideFile path to optional override file
 * @param parallism optional number of parallel threads to run
 * @param dryRun flag to indicate dry run rather than process
 * @param verbose show detailed messaged
 * @param helpRequested show help
 * @param versionRequested show version details
 * @param listPlugsRequested show a list of plugins
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

    /**
     * Loads the command line record
     *
     * @param pluginIds List of plugins
     * @param allPluginsRequested trues if all plugins to be processed else
     * false
     * @param overrideFile path to optional override file
     * @param parallism optional number of parallel threads to run
     * @param dryRun flag to indicate dry run rather than process
     * @param verbose show detailed messaged
     * @param helpRequested show help
     * @param versionRequested show version details
     * @param listPlugsRequested show a list of plugins
     */
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
     *
     * @return
     */
    public boolean informationalRequest() {
        return helpRequested || versionRequested || listPluginsRequested;
    }
}
