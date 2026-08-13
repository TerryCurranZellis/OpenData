/*
 * Copyright © 2026 Terry Curran
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
 * @param allPluginsRequested whether all plugins were requested
 * @param pluginFile optional plugin definition file used only for registration
 * @param parallelism optional plugin parallelism override
 * @param dryRun whether plugin data writes and run-audit rows are disabled
 * @param executeRequested whether plugin execution was explicitly authorised
 * @param verbose whether verbose logging is requested
 * @param helpRequested whether help output was requested
 * @param aboutRequested whether the graphical About window was requested
 * @param listPluginsRequested whether registered plugin listing was requested
 * @param guiRequested - user wants the gui interface
 * @param command requested plugin operation
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public record CommandLineArguments(
        List<String> pluginIds,
        boolean allPluginsRequested,
        Optional<Path> pluginFile,
        OptionalInt parallelism,
        boolean dryRun,
        boolean executeRequested,
        boolean verbose,
        boolean helpRequested,
        boolean aboutRequested,
        boolean listPluginsRequested,
        boolean guiRequested,
        PluginCommand command) {

    /** Validates and normalises record components. */
    public CommandLineArguments {
        pluginIds = List.copyOf(Objects.requireNonNull(pluginIds, "pluginIds"));
        pluginFile = pluginFile == null ? Optional.empty() : pluginFile;
        parallelism = parallelism == null ? OptionalInt.empty() : parallelism;
        command = Objects.requireNonNull(command, "command");
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
     * @return {@code true} for help, About, or plugin listing output
     */
    public boolean informationalRequest() {
        return helpRequested || aboutRequested || listPluginsRequested;
    }

    /**
     * @return whether plugin registration was requested
     */
    public boolean registerRequested() {
        return command == PluginCommand.REGISTER;
    }

    /**
     * @return whether plugin removal was requested
     */
    public boolean unregisterRequested() {
        return command == PluginCommand.UNREGISTER;
    }

    /**
     * @return whether plugin enable was requested
     */
    public boolean enableRequested() {
        return command == PluginCommand.ENABLE;
    }

    /**
     * @return whether plugin disable was requested
     */
    public boolean disableRequested() {
        return command == PluginCommand.DISABLE;
    }

    /**
     * @return whether stored configuration details were requested
     */
    public boolean detailRequested() {
        return command == PluginCommand.DETAIL;
    }

    /**
     * @return whether normal or dry-run plugin execution was requested
     */
    public boolean runRequested() {
        return command == PluginCommand.RUN
                && (executeRequested || dryRun)
                && !informationalRequest();
    }
}
