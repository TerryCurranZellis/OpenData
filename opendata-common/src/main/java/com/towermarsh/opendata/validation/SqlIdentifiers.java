/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.validation;

import java.util.regex.Pattern;

/**
 * Validates and quotes SQL Server identifiers supplied through configuration.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class SqlIdentifiers {

    private static final Pattern SAFE_IDENTIFIER
            = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    private SqlIdentifiers() {
        // Utility class.
    }

    /**
     * Validates one unquoted SQL identifier.
     *
     * @param value identifier value
     * @param name field name used in error messages
     * @return validated identifier
     */
    public static String requireSafe(final String value, final String name) {
        final String result = ValidationRules.requireText(value, name, 128);
        if (!SAFE_IDENTIFIER.matcher(result).matches()) {
            throw new IllegalArgumentException(
                    name + " is not a safe SQL identifier: " + result);
        }
        return result;
    }

    /**
     * Validates and quotes one SQL Server identifier.
     *
     * @param value identifier value
     * @param name field name used in error messages
     * @return bracket-quoted identifier
     */
    public static String quote(final String value, final String name) {
        return '[' + requireSafe(value, name) + ']';
    }

    /**
     * Builds a validated SQL Server schema-qualified table name.
     *
     * @param schema schema name
     * @param table table name
     * @return qualified table name
     */
    public static String qualify(final String schema, final String table) {
        return quote(schema, "schema") + '.' + quote(table, "table");
    }
}
