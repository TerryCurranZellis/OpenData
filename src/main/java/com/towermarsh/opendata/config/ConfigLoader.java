/*
 *  Filename: ConfigLoader.java
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
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.exception.ConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Loads application configuration from properties files.
 *
 * <p>
 * The loader supports loading from:</p>
 * <ul>
 * <li>A supplied external configuration file</li>
 * <li>The default classpath configuration file</li>
 * </ul>
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 21 Jul 2026
 */
public final class ConfigLoader {

    private static final String DEFAULT_CONFIG
            = "application.properties";

    private ConfigLoader() {
        // Utility class
    }

    /**
     * Loads configuration using the default configuration file.
     *
     * @return application configuration
     * @throws ConfigurationException if configuration cannot be loaded
     */
    public static ApplicationConfig load()
            throws ConfigurationException {

        return load(null);
    }

    /**
     * Loads configuration from a supplied file.
     *
     * @param externalFile optional external configuration file
     * @return application configuration
     * @throws ConfigurationException if configuration cannot be loaded
     */
    public static ApplicationConfig load(Path externalFile)
            throws ConfigurationException {

        Properties properties = new Properties();

        try {

            if (externalFile != null) {

                try (InputStream input
                        = Files.newInputStream(externalFile)) {

                    properties.load(input);
                }

            } else {

                try (InputStream input
                        = ConfigLoader.class
                                .getClassLoader()
                                .getResourceAsStream(DEFAULT_CONFIG)) {

                            if (input == null) {
                                throw new IOException(
                                        "Default configuration not found");
                            }

                            properties.load(input);
                        }
            }

        } catch (IOException ex) {

            throw new ConfigurationException(
                    "Unable to load configuration",
                    ex);
        }

        return createConfiguration(properties, externalFile);
    }

    private static ApplicationConfig createConfiguration(
            Properties properties,
            Path configFile) {

        String databaseUrl
                = properties.getProperty("database.url");

        String databaseUser
                = properties.getProperty("database.user");

        String databasePassword
                = properties.getProperty("database.password");

        return new ApplicationConfig(
                configFile,
                databaseUrl,
                databaseUser,
                databasePassword);
    }
}
