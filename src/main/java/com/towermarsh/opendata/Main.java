/*
 * Filename: Main.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata;

import java.io.PrintWriter;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.towermarsh.opendata.app.ApplicationRunStatus;
import com.towermarsh.opendata.cli.CommandLineArgumentsProcessor;
import com.towermarsh.opendata.cli.CommandLineProcessingException;
import com.towermarsh.opendata.config.ConfigurationService;
import com.towermarsh.opendata.exception.ConfigurationException;
import com.towermarsh.opendata.plugin.ClasspathPluginRegistry;
import com.towermarsh.opendata.plugin.PluginDescriptor;
import com.towermarsh.opendata.plugin.PluginRegistry;
import com.towermarsh.opendata.plugin.PluginRegistryException;

/**
 * OpenData Framework command-line entry point.
 *
 * @author Terry Curran
 * @version 23 Jul 2026
 */
public final class Main {

    /**
     * set up logging
     */
    protected static final Logger LOGGER
            = Logger.getLogger(Main.class.getName());

    private Main() {
    }

    /**
     * Main entry point.
     *
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        final var startTime = System.nanoTime();
        var runStatus = ApplicationRunStatus.SUCCESS;

        final var processor = new CommandLineArgumentsProcessor();

        try {
            final var arguments = processor.parse(args);
            final PluginRegistry pluginRegistry
                    = new ClasspathPluginRegistry();

            if (arguments.helpRequested()) {
                processor.printHelp(new PrintWriter(System.out, true));
                return;
            }

            if (arguments.versionRequested()) {
                System.out.println("OpenData Framework 0.1.0-SNAPSHOT");
                return;
            }

            if (arguments.listPluginsRequested()) {
                printPlugins(pluginRegistry);
                return;
            }

            final String pluginId = arguments.pluginId()
                    .orElseThrow(() -> new PluginRegistryException(
                    "A plugin is required for execution."));

            pluginRegistry.requireEnabled(pluginId);

            final var configuration = new ConfigurationService().resolve(arguments);

            LOGGER.info(() -> "Selected plugin: " + configuration.pluginId());
            LOGGER.info(() -> "Dry run: " + configuration.dryRun());
            LOGGER.info(() -> "Resolved properties: " + configuration.runtimeOverrides().size());

            // Hand configuration and descriptor to the plugin execution
            // service / pipeline engine here.
        } catch (CommandLineProcessingException exception) {
            System.err.println(
                    "Command-line error: " + exception.getMessage());
            processor.printHelp(new PrintWriter(System.err, true));
            runStatus = ApplicationRunStatus.COMMAND_LINE_ERROR;
        } catch (PluginRegistryException | ConfigurationException exception) {
            LOGGER.log(Level.SEVERE, "Configuration error: {0}", exception.getMessage());
            runStatus = ApplicationRunStatus.CONFIGURATION_ERROR;
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Unexpected application failure.", exception);
            runStatus = ApplicationRunStatus.APPLICATION_ERROR;
        } finally {
            final var elapsedNanoseconds  = System.nanoTime() - startTime;
            final var duration = Duration.ofNanos(elapsedNanoseconds);

            LOGGER.log( Level.INFO,"Application finished. Status: {0}. Duration: {1}.",
                    new Object[]{
                        runStatus.displayName(),
                        formatDuration(duration)
                    });
        }
    }

    private static void printPlugins(final PluginRegistry pluginRegistry) {
        final var plugins = pluginRegistry.list();

        if (plugins.isEmpty()) {
            System.out.println("No OpenData plugins are installed.");
            return;
        }

        System.out.println("Installed OpenData plugins:");
        for (PluginDescriptor plugin : plugins) {
            System.out.printf(
                    "  %-18s %-10s %s%n",
                    plugin.id(),
                    plugin.enabled() ? "enabled" : "disabled",
                    plugin.displayName());
        }
    }

    private static String formatDuration(final Duration duration) {
        final var hours = duration.toHours();
        final var minutes = duration.toMinutesPart();
        final var seconds = duration.toSecondsPart();
        final var milliseconds = duration.toMillisPart();

        return "%02d:%02d:%02d.%03d"
                .formatted(hours, minutes, seconds, milliseconds);
    }
}
