/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.extract;

import com.towermarsh.opendata.common.database.jdbc.JdbcCompletedFileKeys;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.util.Objects;
import java.util.Set;

/**
 * Reads the names and hashes of successfully processed Octopus statements.
 *
 * @author Terry Curran
 * @version 4.2.0
 */
public final class OctopusProcessedFileRepository {

    /**
     * database connection
     */
    private final DatabaseResourceManager database;

    /**
     * instantiate 
     * @param database database connection 
     */
    public OctopusProcessedFileRepository(DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    /**
     * Returns keys in the form {@code fileName|sha256}.
     *
     * @return a set of processed files
     */
    public Set<String> findProcessedFileKeys() {
        return JdbcCompletedFileKeys.read(
                database, "octopus", "statement_file", "Unable to read processed Octopus statement files. Re-register the octopus plugin to create its tables.");
    }

    /**
     * get the key hash
     * @param fileName filename for statement
     * @param sha256 the hash
     * @return the key has
     */
    public static String key(String fileName, String sha256) {
        return JdbcCompletedFileKeys.key(fileName, sha256);
    }
}
