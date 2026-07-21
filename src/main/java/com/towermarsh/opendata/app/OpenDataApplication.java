/*
 *  Filename: NewClass.java
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
package com.towermarsh.opendata.app;

import com.towermarsh.opendata.config.ApplicationConfig;
import com.towermarsh.opendata.config.ConfigLoader;
import com.towermarsh.opendata.exception.ConfigurationException;
import com.towermarsh.opendata.logging.LoggingManager;

import java.util.logging.Logger;

/**
 * Main application coordinator.
 *
 * <p>
 * This class is responsible for application startup and dependency
 * initialisation.</p>
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 21 Jul 2026
 */
public final class OpenDataApplication {

    @SuppressWarnings("NonConstantLogger")
    private final Logger logger;

    /**
     * Creates the application.
     */
    public OpenDataApplication() {

        logger
                = LoggingManager.getLogger();
    }

    /**
     * Starts the application.
     *
     * @param arguments command line arguments
     * @throws ConfigurationException if configuration fails
     */
    public void start(
            CommandLineArguments arguments)
            throws ConfigurationException {

        ApplicationConfig config
                = ConfigLoader.load(
                        arguments.getConfigFile());

        logger.info(
                "OpenData application started");

        initialiseServices(config);

        logger.info(
                "Application initialisation complete");
    }

    private void initialiseServices(
            ApplicationConfig config) {


        /*
         * Dependency wiring will be added here:
         *
         * Downloader
         * Parser
         * Repository
         * ETL services
         *
         */
    }
}
