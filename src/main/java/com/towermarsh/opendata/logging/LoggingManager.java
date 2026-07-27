/*
 * Filename: LoggingManager.java
 *
 * (c) Copyright 2026 Terry Curran
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

/** Central java.util.logging configuration. */
public final class LoggingManager {
    private static final String LOGGER_NAME = "com.towermarsh.opendata";
    private static final Logger ROOT = Logger.getLogger("");
    private static final Logger LOGGER = Logger.getLogger(LOGGER_NAME);
    private static final Object LOCK = new Object();

    private LoggingManager() {
    }

    /**
     *
     * @param logDirectory
     * @throws IOException
     */
    public static void initialise(final Path logDirectory) throws IOException {
        configure(new LoggingConfiguration(logDirectory, 10_485_760, 10, true), false);
    }

    /**
     *
     * @param configuration
     * @param verbose
     * @throws IOException
     */
    public static void configure(final LoggingConfiguration configuration, final boolean verbose) throws IOException {
        synchronized (LOCK) {
            Files.createDirectories(configuration.directory());
            for (Handler handler : ROOT.getHandlers()) {
                ROOT.removeHandler(handler);
                handler.close();
            }
            final Level level = verbose ? Level.FINE : Level.INFO;
            ROOT.setLevel(level);

            final var formatter = new ContextualLogFormatter();
            final var console = new ConsoleHandler();
            console.setLevel(level);
            console.setFormatter(formatter);
            ROOT.addHandler(console);

            final String pattern = configuration.directory().resolve("opendata-%g.log").toString();
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
     * @return
     */
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
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
