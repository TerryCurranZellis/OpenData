/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.setup;

import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Immutable and validated first-run SQL Server installation settings.
 * Administrative credentials are ephemeral and are never written to disk.
 *
 * @param server SQL Server DNS name or IP address
 * @param port SQL Server TCP port
 * @param database database to create or complete provisioning
 * @param administrator SQL-authenticated server administrator login
 * @param administratorPassword temporary server administrator password
 * @param applicationUser SQL login to create for normal OpenData operations
 * @param applicationPassword password of the application login
 * @param aesKeyFile external AES key file location
 * @param trustServerCertificate whether to skip certificate-chain validation
 * @author Terry Curran
 * @version 4.0.0
 */
public record InitialisationRequest(
        String server,
        int port,
        String database,
        String administrator,
        String administratorPassword,
        String applicationUser,
        String applicationPassword,
        Path aesKeyFile,
        boolean trustServerCertificate) {

    /** Accepted SQL identifiers for database and application login names. */
    private static final Pattern SQL_IDENTIFIER = Pattern.compile("[A-Za-z][A-Za-z0-9_]{0,127}");
    /** Restricts the JDBC URL host component to avoid property injection. */
    private static final Pattern SERVER_NAME = Pattern.compile("[A-Za-z0-9._-]+|[0-9a-fA-F:]+");

    /**
     * Validates all values before the service opens a network connection.
     */
    public InitialisationRequest {
        server = Objects.requireNonNull(server, "server").strip();
        if (!SERVER_NAME.matcher(server).matches()) {
            throw new IllegalArgumentException("Server must be a hostname or an IP address.");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("SQL Server port must be between 1 and 65535.");
        }
        database = validateIdentifier(database, "Database name");
        applicationUser = validateIdentifier(applicationUser, "Application login");
        administrator = requireText(administrator, "Administrator login");
        administratorPassword = requireText(administratorPassword, "Administrator password");
        applicationPassword = requireText(applicationPassword, "Application password");
        aesKeyFile = Objects.requireNonNull(aesKeyFile, "aesKeyFile")
                .toAbsolutePath().normalize();
    }

    /**
     * Generates a JDBC URL with a fixed, separate database identifier.
     *
     * @param databaseName target database
     * @return SQL Server JDBC URL
     */
    public String jdbcUrl(final String databaseName) {
        final var host = server.contains(":") ? "[" + server + "]" : server;
        return "jdbc:sqlserver://" + host + ":" + port
                + ";databaseName=" + validateIdentifier(databaseName, "Database name")
                + ";encrypt=true;trustServerCertificate=" + trustServerCertificate
                + ";loginTimeout=15";
    }

    /**
     * Validates a simple SQL identifier accepted by this wizard.
     *
     * @param value source string
     * @param label field name for validation diagnostics
     * @return trimmed identifier
     */
    private static String validateIdentifier(final String value, final String label) {
        final var text = requireText(value, label);
        if (!SQL_IDENTIFIER.matcher(text).matches()) {
            throw new IllegalArgumentException(label + " must start with a letter and contain only letters, digits and underscores.");
        }
        return text;
    }

    /**
     * Rejects empty form entries.
     *
     * @param value source value
     * @param label field name
     * @return trimmed non-empty text
     */
    private static String requireText(final String value, final String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.strip();
    }
}
