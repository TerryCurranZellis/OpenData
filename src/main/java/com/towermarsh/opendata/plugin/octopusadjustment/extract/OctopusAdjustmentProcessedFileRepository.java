/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.extract;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Reads completed Octopus adjustment source identities.
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
public final class OctopusAdjustmentProcessedFileRepository {

    private final DatabaseResourceManager database;

    /**
     * Creates the repository.
     *
     * @param database database resource manager
     * @since 3.1.0
     */
    public OctopusAdjustmentProcessedFileRepository(final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    /**
     * Returns completed source keys in {@code lower-case-file-name|lower-case-hash} form.
     *
     * @return immutable completed-key set
     * @since 3.1.0
     */
    public Set<String> findProcessedFileKeys() {
        final String sql = """
                SELECT [file_name], [sha256]
                  FROM [octopus].[adjustment_file]
                 WHERE [status] = 'COMPLETED'
                """;
        try (var connection = database.getConnection();
             var statement = connection.prepareStatement(sql);
             var results = statement.executeQuery()) {
            final Set<String> keys = new HashSet<>();
            while (results.next()) {
                keys.add(key(results.getString(1), results.getString(2)));
            }
            return Set.copyOf(keys);
        } catch (SQLException exception) {
            throw new DatabaseAccessException(
                    "Unable to read processed Octopus adjustment files. Run sql/011-create-octopus-adjustment-tables.sql.",
                    exception);
        }
    }

    /**
     * Builds the normalised completion key.
     *
     * @param fileName source filename
     * @param sha256 source hash
     * @return normalised key
     * @since 3.1.0
     */
    public static String key(final String fileName, final String sha256) {
        Objects.requireNonNull(fileName, "fileName");
        Objects.requireNonNull(sha256, "sha256");
        return fileName.toLowerCase(Locale.ROOT) + "|" + sha256.toLowerCase(Locale.ROOT);
    }
}
