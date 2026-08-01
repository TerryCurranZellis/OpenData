/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.logging;

import com.towermarsh.opendata.config.LoggingConfiguration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central java.util.logging configuration.
 *
 *
 * @author Terry Curran
 * @version 1.0.0
 */
@SuppressWarnings("ClassWithMultipleLoggers")
public final class LoggingManager {

    private static final String LOGGER_NAME = "com.towermarsh.opendata";
    private static final Logger ROOT = Logger.getLogger("");
    private static final Logger LOGGER = Logger.getLogger(LOGGER_NAME);
    private static final Object LOCK = new Object();

    /**
     * Prevents instantiation of this utility class.
     */
    private LoggingManager() {
    }

    /**
     *
     * Initialises logging with the default rotating-file configuration.
     *
     * @param logDirectory log output directory
     * @throws IOException if logging cannot be initialised
     *
     */
    public static void initialise(final Path logDirectory) throws IOException {
        configure(new LoggingConfiguration(logDirectory, 10_485_760, 10, true), false);
    }

    /**
     *
     * Configures console and file logging handlers.
     *
     * @param configuration logging configuration
     * @param verbose whether `FINE` logging should be enabled
     * @throws IOException if a file handler cannot be created
     */
    public static void configure(final LoggingConfiguration configuration, final boolean verbose) throws IOException {
        synchronized (LOCK) {
            Files.createDirectories(configuration.directory());
            for (var handler : ROOT.getHandlers()) {
                ROOT.removeHandler(handler);
                handler.close();
            }
            final var level = verbose ? Level.FINE : Level.INFO;
            ROOT.setLevel(level);

            final var formatter = new ContextualLogFormatter();
            final var console = new ConsoleHandler();
            console.setLevel(level);
            console.setFormatter(formatter);
            ROOT.addHandler(console);

            final var pattern = configuration.directory().resolve("opendata-%g.log").toString();
            final var file = new FileHandler(
                    pattern,
                    configuration.fileLimitBytes(),
                    configuration.fileCount(),
                    configuration.append());
            file.setLevel(level);
            file.setFormatter(formatter);
            ROOT.addHandler(file);
            LOGGER.setLevel(level);
        }
    }

    /**
     *
     * Returns the shared application logger.
     *
     * @return shared application logger
     *
     */
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     *
     * Flushes and closes configured logging handlers.
     *
     */
    public static void shutdown() {
        synchronized (LOCK) {
            for (Handler handler : ROOT.getHandlers()) {
                handler.flush();
                handler.close();
                ROOT.removeHandler(handler);
            }
        }
    }
}
