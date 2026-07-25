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

/** Immutable command-line arguments for one invocation. */
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

    public boolean informationalRequest() {
        return helpRequested || versionRequested || listPluginsRequested;
    }
}
