/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.extract;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** Reads the names and hashes of successfully processed Octopus statements. */
public final class OctopusProcessedFileRepository {
    private final DatabaseResourceManager database;

    public OctopusProcessedFileRepository(final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    /** Returns keys in the form {@code fileName|sha256}. */
    public Set<String> findProcessedFileKeys() {
        final String sql = """
                SELECT [file_name], [sha256]
                  FROM [octopus].[statement_file]
                 WHERE [status] = 'COMPLETED'
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            final Set<String> keys = new HashSet<>();
            while (results.next()) {
                keys.add(key(results.getString(1), results.getString(2)));
            }
            return Set.copyOf(keys);
        } catch (SQLException exception) {
            throw new DatabaseAccessException(
                    "Unable to read processed Octopus statement files. Run sql/007a-create-octopus-schema.sql.",
                    exception);
        }
    }

    public static String key(final String fileName, final String sha256) {
        return fileName.toLowerCase(java.util.Locale.ROOT) + "|" + sha256.toLowerCase(java.util.Locale.ROOT);
    }
}
