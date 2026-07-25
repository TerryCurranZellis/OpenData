/*
 * Filename: Main.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata;

import com.towermarsh.opendata.app.ExecutionStatus;
import com.towermarsh.opendata.app.OpenDataApplication;
import com.towermarsh.opendata.cli.CommandLineArgumentsProcessor;
import com.towermarsh.opendata.cli.CommandLineProcessingException;
import com.towermarsh.opendata.config.OpenDataConfigurationException;
import com.towermarsh.opendata.logging.LoggingManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OpenData application entry point.
 */
public final class Main {

    private Main() {
    }

    /**
     * Starts the application without terminating the JVM explicitly.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final var startedAt = Instant.now();
        final var processor = new CommandLineArgumentsProcessor();
        var status = ExecutionStatus.NOT_STARTED;
        var logger = Logger.getLogger(Main.class.getName());
        try {
            LoggingManager.initialise(Path.of("logs"));
            logger = LoggingManager.getLogger();
            final var arguments = processor.parse(args);
            status = new OpenDataApplication().start(arguments, processor);
        } catch (CommandLineProcessingException exception) {
            status = ExecutionStatus.COMMAND_LINE_ERROR;
            System.err.println("Command-line error: " + exception.getMessage());
            processor.printHelp(new PrintWriter(System.err, true));
        } catch (OpenDataConfigurationException exception) {
            status = ExecutionStatus.CONFIGURATION_ERROR;
            logger.log(Level.SEVERE, "Configuration error: {0}", exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            status = ExecutionStatus.INTERRUPTED;
            logger.log(Level.WARNING, "Application execution was interrupted.", exception);
        } catch (IOException exception) {
            status = ExecutionStatus.APPLICATION_FAILURE;
            logger.log(Level.SEVERE, "Unable to initialise application logging.", exception);
        } catch (RuntimeException exception) {
            status = ExecutionStatus.RUNTIME_ERROR;
            logger.log(Level.SEVERE, "Unexpected application failure.", exception);
        } finally {
            final Duration duration = Duration.between(startedAt, Instant.now());
            logger.log(Level.INFO,
                    "OpenData finished with status {0}; duration {1} ms",
                    new Object[]{status.name(), duration.toMillis()});
            LoggingManager.shutdown();
        }
    }
}
