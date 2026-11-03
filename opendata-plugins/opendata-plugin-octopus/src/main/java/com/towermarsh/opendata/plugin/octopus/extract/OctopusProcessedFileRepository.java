/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.extract;

import com.towermarsh.opendata.common.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Reads the names and hashes of successfully processed Octopus statements.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public class OctopusProcessedFileRepository {

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
        var sql = """
                SELECT [file_name], [sha256]
                  FROM [octopus].[statement_file]
                 WHERE [status] = 'COMPLETED'
                """;
        try (var connection = database.getConnection(); 
             var statement = connection.prepareStatement(sql); 
             var results = statement.executeQuery()) {
            Set<String> keys = new HashSet<>();
            while (results.next()) {
                keys.add(key(results.getString(1), results.getString(2)));
            }
            return Set.copyOf(keys);
        } catch (SQLException exception) {
            throw new DatabaseAccessException(
                    "Unable to read processed Octopus statement files. Re-register the octopus plugin to create its tables.",
                    exception);
        }
    }

    /**
     * get the key hash
     * @param fileName filename for statement
     * @param sha256 the hash
     * @return the key has
     */
    public static String key(String fileName, String sha256) {
        return fileName.toLowerCase(Locale.ROOT) + "|" + sha256.toLowerCase(Locale.ROOT);
    }
}
