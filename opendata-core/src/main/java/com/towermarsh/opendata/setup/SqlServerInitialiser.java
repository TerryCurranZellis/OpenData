/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.setup;

import com.towermarsh.opendata.database.SqlBatchParser;
import com.towermarsh.opendata.config.PluginRegistration;
import com.towermarsh.opendata.core.plugin.PluginRegistrarLocator;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.config.OpenDataConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Creates an SQL Server database, SQL login, core schema, and permissions.
 * Administrator credentials remain in memory and are not persisted.
 * SQL resources are idempotent and run using administrator connections.
 *
 * @author Terry Curran
 * @since 4.0.0
 * @version 4.1.0
 */
public final class SqlServerInitialiser {

    /** Ordered core DDL resources, excluding CLI-only login creation scripts. */
    private static final List<String> CORE_SCRIPTS = List.of(
            "sql/setup/002-create-core-schema.sql",
            "sql/setup/003-create-configuration-store.sql",
            "sql/setup/003a-create-plugin-registry.sql",
            "sql/setup/006-create-plugin-run-audit.sql");

    /**
     * Creates an SQL Server initialiser.
     */
    public SqlServerInitialiser() {
        // Intentionally stateless.
    }

    /**
     * Creates the database and application SQL login when absent, then runs
     * the established core DDL scripts and grants the access needed for plugin
     * schema installation. On a retry, existing objects are retained.
     *
     * @param request validated first-run settings
     * @param progress status callback, never sent credentials
     */
    public void initialise(final InitialisationRequest request, final Consumer<String> progress) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(progress, "progress");
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            progress.accept("Connecting to SQL Server using the temporary administrator login...");
            try (var master = DriverManager.getConnection(
                    request.jdbcUrl("master"), request.administrator(), request.administratorPassword())) {
                createDatabase(master, request);
                createSqlLogin(master, request);
            }
            try (var database = DriverManager.getConnection(
                    request.jdbcUrl(request.database()), request.administrator(), request.administratorPassword())) {
                progress.accept("Creating OpenData core tables and schema...");
                for (var resource : CORE_SCRIPTS) {
                    runResource(database, resource, request.database());
                }
                progress.accept("Granting SQL permissions to the OpenData application login...");
                createApplicationUser(database, request);
            }
        } catch (SQLException | ClassNotFoundException exception) {
            throw new OpenDataConfigurationException(
                    "SQL Server initialisation failed. Verify login permissions, TLS and network settings. "
                    + exception.getMessage(), exception);
        }
    }

    /**
     * Uses administrator privileges for plugin DDL and GRANT statements.
     * Database metadata is still subsequently persisted by the normal
     * application login; the administrative password is never persisted.
     *
     * @param request temporary administrator connection settings
     * @param plugins validated plugins to initialise
     * @param progress status callback
     */
    public void installPluginSchemas(final InitialisationRequest request,
            final List<PluginRegistration> plugins, final Consumer<String> progress) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(plugins, "plugins");
        Objects.requireNonNull(progress, "progress");
        try (var adminDatabase = new AdministratorDatabase(request)) {
            final var locator = new PluginRegistrarLocator();
            for (var registration : plugins) {
                progress.accept("Installing database tables for " + registration.descriptor().id() + "...");
                locator.install(registration.descriptor(), adminDatabase);
            }
        }
    }

    /**
     * Minimal connection-owning adapter for administrative plugin DDL.
     * Every plugin registration hook borrows a fresh SQL Server connection.
     */
    private static final class AdministratorDatabase implements DatabaseResourceManager {

        private final InitialisationRequest request;

        /**
         * Stores the temporary administrator credentials for this setup run.
         *
         * @param request first-run settings
         */
        private AdministratorDatabase(final InitialisationRequest request) {
            this.request = request;
        }

        /**
         * Opens one administrator connection to the new database.
         *
         * @return database connection owned by the caller
         * @throws SQLException if the connection cannot be established
         */
        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(request.jdbcUrl(request.database()),
                    request.administrator(), request.administratorPassword());
        }

        /** Closes this connection factory; connections are scoped per call. */
        @Override
        public void close() {
            // No pooled connections or state to close.
        }
    }

    /**
     * Creates the target database if it does not already exist.
     *
     * @param master connection to master
     * @param request install request
     * @throws SQLException when SQL Server rejects the statement
     */
    private static void createDatabase(final Connection master, final InitialisationRequest request)
            throws SQLException {
        final var db = request.database();
        try (var check = master.prepareStatement("SELECT DB_ID(?)")) {
            check.setString(1, db);
            try (var result = check.executeQuery()) {
                if (result.next() && result.getObject(1) != null) {
                    return;
                }
            }
        }
        try (var sql = master.createStatement()) {
            sql.execute("CREATE DATABASE " + identifier(db));
        }
    }

    /**
     * Creates the application login if missing, without changing an existing
     * login or resetting its password.
     *
     * @param master connection to master
     * @param request install request
     * @throws SQLException when login creation fails
     */
    @SuppressFBWarnings(value = "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE", justification =
            "CREATE LOGIN does not accept JDBC parameters for identifiers or its DDL password; "
            + "InitialisationRequest restricts login identifiers to letters, digits and underscores "
            + "and the password SQL literal has apostrophes escaped.")
    private static void createSqlLogin(final Connection master, final InitialisationRequest request)
            throws SQLException {
        try (var check = master.prepareStatement(
                "SELECT COUNT(*) FROM sys.server_principals WHERE name = ?")) {
            check.setString(1, request.applicationUser());
            try (var result = check.executeQuery()) {
                result.next();
                if (result.getInt(1) != 0) {
                    return;
                }
            }
        }
        // SQL Server CREATE LOGIN forbids bind placeholders in the PASSWORD DDL.
        // Escape the SQL literal and restrict the login to a validated identifier.
        final var command = "CREATE LOGIN " + identifier(request.applicationUser())
                + " WITH PASSWORD = N'" + request.applicationPassword().replace("'", "''")
                + "', CHECK_POLICY = ON, CHECK_EXPIRATION = OFF";
        try (var sql = master.createStatement()) {
            sql.execute(command);
        }
    }

    /**
     * Creates the application database principal and grants registry, core,
     * and schema-DDL rights required by the existing plugin registration API.
     *
     * @param connection administrator connection to target database
     * @param request install request
     * @throws SQLException if permissions cannot be granted
     */
    private static void createApplicationUser(
            final Connection connection, final InitialisationRequest request) throws SQLException {
        final var login = identifier(request.applicationUser());
        try (var statement = connection.createStatement()) {
            statement.execute("IF DATABASE_PRINCIPAL_ID(N'" + request.applicationUser()
                    + "') IS NULL CREATE USER " + login + " FOR LOGIN " + login);
            statement.execute("IF DATABASE_PRINCIPAL_ID(N'opendata_app') IS NULL "
                    + "CREATE ROLE [opendata_app] AUTHORIZATION [dbo]");
            statement.execute("IF IS_ROLEMEMBER(N'opendata_app', N'" + request.applicationUser()
                    + "') <> 1 ALTER ROLE [opendata_app] ADD MEMBER " + login);
            // Existing SQL registration hooks issue CREATE SCHEMA/TABLE; the app
            // must retain DDL rights for later plugin registrations as designed.
            statement.execute("IF IS_ROLEMEMBER(N'db_ddladmin', N'" + request.applicationUser()
                    + "') <> 1 ALTER ROLE [db_ddladmin] ADD MEMBER " + login);
            statement.execute("GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::[core] TO [opendata_app]");
        }
    }

    /**
     * Reads and executes each GO-delimited script, replacing only the original
     * literal OpenData database directive for custom database names.
     *
     * @param connection target database administrator connection
     * @param resource classpath SQL resource
     * @param dbName validated database identifier
     * @throws SQLException if an SQL batch fails
     */
    private static void runResource(
            final Connection connection, final String resource, final String dbName) throws SQLException {
        final String script;
        try (var source = SqlServerInitialiser.class.getClassLoader().getResourceAsStream(resource)) {
            if (source == null) {
                throw new OpenDataConfigurationException("Missing core SQL setup resource: " + resource);
            }
            script = new String(source.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("USE [OpenData];", "USE " + identifier(dbName) + ";");
        } catch (IOException exception) {
            throw new OpenDataConfigurationException("Unable to read core SQL setup resource " + resource, exception);
        }
        var batchNumber = 0;
        for (var batch : SqlBatchParser.batches(script)) {
            batchNumber++;
            try (var statement = connection.createStatement()) {
                statement.execute(batch);
            } catch (SQLException exception) {
                throw new SQLException("Core SQL " + resource + " batch " + batchNumber
                        + " failed: " + exception.getMessage(), exception);
            }
        }
    }

    /**
     * Quotes a validated SQL identifier.
     *
     * @param value database or login identifier
     * @return bracket-delimited identifier
     */
    private static String identifier(final String value) {
        return "[" + value + "]";
    }
}
