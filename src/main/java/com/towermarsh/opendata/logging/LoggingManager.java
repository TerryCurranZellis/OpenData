/*
 *  Filename: LoggingManager.java
 * 
 *  (C) Copyright Terry Curran 2026. All rights reserved
 * 
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 * 
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 * 
 *  The author may be contacted by email to the following address:
 * 
 *  terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.logging.*;

/**
 * Central logging configuration manager.
 *
 * <p>
 * All OpenData application components should obtain loggers through this class
 * rather than creating their own configuration.</p>
 *
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 21 Jul 2026
 */
public class LoggingManager {

    private static final String APPLICATION_NAME
            = "OpenData";

    private static Logger logger;

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

        Logger rootLogger
                = Logger.getLogger("");

        rootLogger.setLevel(Level.INFO);

        FileHandler fileHandler
                = new FileHandler(
                        logDirectory
                                .resolve(
                                        APPLICATION_NAME
                                        + "-"
                                        + LocalDate.now()
                                        + ".log")
                                .toString(),
                        true);

        fileHandler.setFormatter(
                new SimpleFormatter());

        rootLogger.addHandler(fileHandler);

        logger
                = Logger.getLogger(APPLICATION_NAME);

        logger.info(
                "Logging initialised");
    }

    /**
     * Returns the application logger.
     *
     * @return logger instance
     */
    public static Logger getLogger() {

        if (logger == null) {

            logger
                    = Logger.getLogger(APPLICATION_NAME);
        }

        return logger;
    }
}
