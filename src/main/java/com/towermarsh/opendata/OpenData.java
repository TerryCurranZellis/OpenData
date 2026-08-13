/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata;

import com.towermarsh.opendata.app.ExecutionStatus;
import com.towermarsh.opendata.app.OpenDataApplication;
import com.towermarsh.opendata.cli.CommandLineArguments;
import com.towermarsh.opendata.cli.CommandLineArgumentsProcessor;
import com.towermarsh.opendata.cli.CommandLineProcessingException;
import com.towermarsh.opendata.config.OpenDataConfigurationException;
import com.towermarsh.opendata.config.PluginDefinitionException;
import com.towermarsh.opendata.database.DatabaseException;
import com.towermarsh.opendata.logging.LoggingManager;
import com.towermarsh.opendata.gui.GuiLauncher;
import static com.towermarsh.opendata.ui.AboutDialog.showAndWait;
import com.towermarsh.opendata.ui.ApplicationInfo;
import com.towermarsh.opendata.ui.StartupSplashScreen;
import static com.towermarsh.opendata.util.DurationFormatter.formatElapsed;
import static com.towermarsh.opendata.util.ExceptionMessages.rootCauseMessage;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OpenData application entry point.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
@SuppressWarnings("deprecation")
public final class OpenData {

    /**
     * Instantiate main code
     */
    private OpenData() {
    }

    /**
     * setup a default logger
     */
    @SuppressWarnings("NonConstantLogger")
    private static Logger logger = Logger.getLogger(OpenData.class.getName());

    /**
     * Starts the application without terminating the JVM explicitly.
     *
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        final var startedAt = Instant.now();
        final var processor = new CommandLineArgumentsProcessor();
        final var splash = new StartupSplashScreen();
        var status = ExecutionStatus.NOT_STARTED;
        try {
            enableUTF8Console();
            LoggingManager.initialise(Path.of("logs"));
            logger = LoggingManager.getLogger();
            final String[] effectiveArguments
                    = args.length == 0
                            ? new String[]{"--gui"}
                    : args;
            final var arguments = processor.parse(effectiveArguments);
            LoggingManager.setVerbose(arguments.verbose());
            logStartup(ApplicationInfo.current(), arguments);
            if (arguments.guiRequested()) {
                GuiLauncher.launch(new String[0]);
                status = ExecutionStatus.SUCCESS;
            } else if (arguments.aboutRequested()) {
                showAndWait(ApplicationInfo.current());
                status = ExecutionStatus.SUCCESS;
            } else {
                if (arguments.runRequested()) {
                    splash.show();
                }
                status = new OpenDataApplication().start(arguments, processor);
            }
        } catch (CommandLineProcessingException exception) {
            status = ExecutionStatus.COMMAND_LINE_ERROR;
            logger.log(Level.SEVERE, "Command-line error: {0}", rootCauseMessage(exception));
            processor.printHelp(new PrintWriter(System.err, true, StandardCharsets.UTF_8));
        } catch (PluginDefinitionException | OpenDataConfigurationException exception) {
            status = ExecutionStatus.CONFIGURATION_ERROR;
            logger.log(Level.SEVERE, "Configuration error: {0}", rootCauseMessage(exception));
        } catch (DatabaseException exception) {
            status = ExecutionStatus.DATABASE_FAILURE;
            logger.log(Level.SEVERE, "Database failure: {0}", rootCauseMessage(exception));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            status = ExecutionStatus.INTERRUPTED;
            logger.log(Level.WARNING, "Application execution was interrupted: {0}", rootCauseMessage(exception));
        } catch (IOException exception) {
            status = ExecutionStatus.APPLICATION_FAILURE;
            logger.log(Level.SEVERE, "Application I/O failure: {0}", rootCauseMessage(exception));
        } catch (Exception exception) {
            status = ExecutionStatus.APPLICATION_FAILURE;
            logger.log(Level.SEVERE, "Unexpected application failure: {0}", rootCauseMessage(exception));
            logger.log(Level.FINE, "Unexpected application failure details.", exception);
        } finally {
            final var duration = Duration.between(startedAt, Instant.now());
            logger.log(Level.INFO, "OpenData finished with status {0}; duration {1}",
                    new Object[]{status.displayName(), formatElapsed(duration)});
            LoggingManager.shutdown();
        }
    }

    /**
     * Logs non-sensitive application identity and invocation details.
     *
     * @param information product information
     * @param arguments command line arguments
     */
    private static void logStartup(final ApplicationInfo information, final CommandLineArguments arguments) {
        logger.log(Level.INFO, "{0} {1} starting", new Object[]{information.productName(), information.version()});
        logger.log(Level.INFO,
                "Runtime: {0}; OS: {1} {2}; workingDirectory={3}",
                new Object[]{
                    information.runtime(),
                    System.getProperty("os.name", "unknown"),
                    System.getProperty("os.version", "unknown"),
                    Path.of("").toAbsolutePath().normalize()
                });
        if (!arguments.guiRequested()) {
        logger.log(Level.INFO,
                "Invocation: command={0}; dryRun={1}; verbose={2}",
                new Object[]{arguments.command().displayName(), arguments.dryRun(), arguments.verbose()});
        }
    }

    /**
     * Redirect {@link System#out} and {@link System#err} to UTF-8 so that
     * characters render correctly on Windows consoles.
     */
    private static void enableUTF8Console() {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.log(Level.INFO, "UTF-8 console could not be enabled", e);
        }
    }
}
