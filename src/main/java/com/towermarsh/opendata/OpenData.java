/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata;

import com.towermarsh.opendata.app.ExecutionStatus;
import com.towermarsh.opendata.app.OpenDataApplication;
import com.towermarsh.opendata.cli.CommandLineArgumentsProcessor;
import com.towermarsh.opendata.cli.CommandLineProcessingException;
import com.towermarsh.opendata.config.OpenDataConfigurationException;
import com.towermarsh.opendata.config.PluginDefinitionException;
import com.towermarsh.opendata.database.DatabaseException;
import com.towermarsh.opendata.logging.LoggingManager;
import com.towermarsh.opendata.ui.AboutDialog;
import com.towermarsh.opendata.ui.ApplicationInfo;
import com.towermarsh.opendata.ui.StartupSplashScreen;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OpenData application entry point.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class OpenData {

    private OpenData() {
    }

    /**
     * Starts the application without terminating the JVM explicitly.
     *
     * @param args the command line argument array
     */
    public static void main(final String[] args) {
        final var startedAt = Instant.now();
        final var processor = new CommandLineArgumentsProcessor();
        final var splash = new StartupSplashScreen();
        var status = ExecutionStatus.NOT_STARTED;
        var logger = Logger.getLogger(OpenData.class.getName());
        try {
            LoggingManager.initialise(Path.of("logs"));
            logger = LoggingManager.getLogger();
            final var arguments = processor.parse(args);
            if (arguments.aboutRequested()) {
                AboutDialog.showAndWait(ApplicationInfo.current());
                status = ExecutionStatus.SUCCESS;
            } else {
                splash.show();
                status = new OpenDataApplication().start(arguments, processor);
            }
        } catch (CommandLineProcessingException exception) {
            status = ExecutionStatus.COMMAND_LINE_ERROR;
            logger.log(Level.SEVERE, "Command-line error: {0}", messageFor(exception));
            processor.printHelp(new PrintWriter(System.err, true));
        } catch (PluginDefinitionException | OpenDataConfigurationException exception) {
            status = ExecutionStatus.CONFIGURATION_ERROR;
            logger.log(Level.SEVERE, "Configuration error: {0}", messageFor(exception));
        } catch (DatabaseException exception) {
            status = ExecutionStatus.DATABASE_FAILURE;
            logger.log(Level.SEVERE, "Database failure: {0}", messageFor(exception));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            status = ExecutionStatus.INTERRUPTED;
            logger.log(Level.WARNING, "Application execution was interrupted: {0}", messageFor(exception));
        } catch (IOException exception) {
            status = ExecutionStatus.APPLICATION_FAILURE;
            logger.log(Level.SEVERE, "Application I/O failure: {0}", messageFor(exception));
        } catch (Exception exception) {
            status = ExecutionStatus.APPLICATION_FAILURE;
            logger.log(Level.SEVERE, "Unexpected application failure: {0}", messageFor(exception));
            logger.log(Level.FINE, "Unexpected application failure details.", exception);
        } finally {
            final var duration = Duration.between(startedAt, Instant.now());
            logger.log(Level.INFO, "OpenData finished with status {0}; duration {1} ms",
                    new Object[]{status.displayName(), duration.toMillis()});
            LoggingManager.shutdown();
        }
    }

    /**
     * display the exception message
     *
     * @param exception the exception details
     */
    private static String messageFor(final Throwable exception) {
        var current = exception;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        final var message = current.getMessage();
        return message == null || message.isBlank()
                ? current.getClass().getSimpleName()
                : message;
    }
}
