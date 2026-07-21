/*
 *  Filename: ApplicationConfig.java
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

import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents the runtime configuration for the OpenData application.
 *
 * <p>
 * This class contains configuration values loaded from the default
 * configuration source or an optional user supplied override file.</p>
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 21 Jul 2026
 */
public final class ApplicationConfig {

    private final Path configFile;
    private final String databaseUrl;
    private final String databaseUser;
    private final String databasePassword;

    /**
     * Creates an application configuration instance.
     *
     * @param configFile optional configuration file location
     * @param databaseUrl JDBC database connection URL
     * @param databaseUser database username
     * @param databasePassword database password
     */
    public ApplicationConfig(
            Path configFile,
            String databaseUrl,
            String databaseUser,
            String databasePassword) {

        this.configFile = configFile;
        this.databaseUrl = Objects.requireNonNull(
                databaseUrl,
                "databaseUrl cannot be null");

        this.databaseUser = Objects.requireNonNull(
                databaseUser,
                "databaseUser cannot be null");

        this.databasePassword = Objects.requireNonNull(
                databasePassword,
                "databasePassword cannot be null");
    }

    /**
     * Returns the optional configuration file.
     *
     * @return configuration file path or null
     */
    public Path getConfigFile() {
        return configFile;
    }

    /**
     * Returns the JDBC database URL.
     *
     * @return database URL
     */
    public String getDatabaseUrl() {
        return databaseUrl;
    }

    /**
     * Returns the database username.
     *
     * @return username
     */
    public String getDatabaseUser() {
        return databaseUser;
    }

    /**
     * Returns the database password.
     *
     * @return password
     */
    public String getDatabasePassword() {
        return databasePassword;
    }
}
