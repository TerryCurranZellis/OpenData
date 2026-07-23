/*
 * Filename: LoggingManager.java
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
package com.towermarsh.opendata.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Central logging configuration manager.
 *
 * <p>
 * All OpenData application components should obtain loggers through this class
 * rather than creating their own configuration.</p>
 *
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public class LoggingManager {

    private static final String APPLICATION_NAME
            = "OpenData";

    /**
     * set the logger name to this class
     */
    protected static final Logger logger = Logger.getLogger(LoggingManager.class.getName());

    private LoggingManager() {
        // Utility class
    }

    /**
     * Initialises application logging.
     *
     * @param logDirectory directory where log files are written
     * @throws IOException if logging cannot be configured
     */
    public static void initialise(Path logDirectory)
            throws IOException {
        if (!Files.exists(logDirectory)) {
            Files.createDirectories(logDirectory);
        }
        var rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
        var fileHandler
                = new FileHandler(
                        logDirectory
                                .resolve(
                                        APPLICATION_NAME
                                        + "-"
                                        + LocalDate.now()
                                        + ".log")
                                .toString(),
                        true);
        fileHandler.setFormatter(new SimpleFormatter());
        rootLogger.addHandler(fileHandler);
        logger.info("Logging initialised");
    }

    /**
     * Returns the application logger.
     *
     * @return logger instance
     */
    public static Logger getLogger() {
        return logger;
    }
}
