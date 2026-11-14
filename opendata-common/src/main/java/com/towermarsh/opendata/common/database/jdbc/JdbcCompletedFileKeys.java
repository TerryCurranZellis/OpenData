/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.database.jdbc;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.validation.SqlIdentifiers;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Queries completed source identities from plugin-owned SQL Server tables.
 *
 * <p>Schema and table names are validated before substitution into the query.
 * The shared implementation avoids duplication of JDBC lookup loops.</p>
 *
 * @author Terry Curran
 * @since 4.2.0
 * @version 4.2.0
 */
public final class JdbcCompletedFileKeys {

    private JdbcCompletedFileKeys() {
    }

    /**
     * Loads the immutable set of keys for completed sources.
     *
     * @param database provider of JDBC connections
     * @param schema SQL Server schema identifier
     * @param table SQL Server table identifier
     * @param errorMessage message used if the JDBC lookup fails
     * @return immutable set of normalised source keys
     */
    public static Set<String> read(
            DatabaseResourceManager database,
            String schema,
            String table,
            String errorMessage) {
        Objects.requireNonNull(database, "database");
        Objects.requireNonNull(errorMessage, "errorMessage");
        var sql = "SELECT [file_name], [sha256] FROM "
                + SqlIdentifiers.qualify(schema, table)
                + " WHERE [status] = 'COMPLETED'";
        try (var connection = database.getConnection();
                var statement = connection.prepareStatement(sql);
                var results = statement.executeQuery()) {
            var keys = new HashSet<String>();
            while (results.next()) {
                keys.add(key(results.getString(1), results.getString(2)));
            }
            return Set.copyOf(keys);
        } catch (SQLException exception) {
            throw new DatabaseAccessException(errorMessage, exception);
        }
    }

    /**
     * Normalises a file identity for case-insensitive matching.
     *
     * @param fileName source filename
     * @param sha256 source content digest
     * @return lower-case file and digest joined by a vertical bar
     */
    public static String key(String fileName, String sha256) {
        return Objects.requireNonNull(fileName, "fileName").toLowerCase(Locale.ROOT)
                + "|" + Objects.requireNonNull(sha256, "sha256").toLowerCase(Locale.ROOT);
    }
}
