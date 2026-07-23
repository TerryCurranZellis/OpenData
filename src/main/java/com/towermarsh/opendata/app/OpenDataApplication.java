/*
 * Filename: OpenDataApplication.java
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
package com.towermarsh.opendata.app;


import com.towermarsh.opendata.config.ApplicationConfig;
import com.towermarsh.opendata.config.ConfigurationLoader;
import com.towermarsh.opendata.exception.ConfigurationException;
import com.towermarsh.opendata.logging.LoggingManager;
import java.util.logging.Logger;
import com.towermarsh.opendata.cli.CommandLineArguments;

/**
 * Main application coordinator.
 *
 * <p>
 * This class is responsible for application startup and dependency
 * initialisation.</p>
 *
 * @author Terry Curran
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

        var config = new ConfigurationLoader().load(arguments);

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
