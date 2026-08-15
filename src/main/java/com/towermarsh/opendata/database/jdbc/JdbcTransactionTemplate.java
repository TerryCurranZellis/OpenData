/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database.jdbc;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Provides consistent JDBC transaction, rollback, and pooled-session cleanup.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class JdbcTransactionTemplate {

    private static final JdbcConnectionCleanup NO_CLEANUP = connection -> {
        // No connection-scoped state to remove.
    };

    private final DatabaseResourceManager database;

    /**
     * Creates a transaction template.
     *
     * @param database database resource manager
     */
    public JdbcTransactionTemplate(final DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    /**
     * Executes work in one transaction.
     *
     * @param failureMessage message used when checked database work fails
     * @param transaction transactional work
     * @param <T> result type
     * @return transaction result
     */
    public <T> T execute(
            final String failureMessage,
            final JdbcTransaction<T> transaction) {
        return execute(failureMessage, transaction, NO_CLEANUP);
    }

    /**
     * Executes work in one transaction and removes connection-scoped state
     * before returning the connection to its pool.
     *
     * @param failureMessage message used when checked database work fails
     * @param transaction transactional work
     * @param cleanup pooled-session cleanup
     * @param <T> result type
     * @return transaction result
     */
    public <T> T execute(
            final String failureMessage,
            final JdbcTransaction<T> transaction,
            final JdbcConnectionCleanup cleanup) {
        Validation.requireArguments(failureMessage, transaction, cleanup);
        try (var connection = database.getConnection()) {
            return execute(connection, transaction, cleanup);
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new DatabaseAccessException(failureMessage, exception);
        }
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    private static <T> T execute(
            final Connection connection,
            final JdbcTransaction<T> transaction,
            final JdbcConnectionCleanup cleanup) throws Exception {
        final boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        Exception primaryFailure = null;
        try {
            final T result = transaction.execute(connection);
            connection.commit();
            return result;
        } catch (Exception exception) {
            primaryFailure = exception;
            rollback(connection, exception);
            throw exception;
        } finally {
            final SQLException cleanupFailure
                    = cleanup(connection, cleanup, originalAutoCommit);
            if (cleanupFailure != null) {
                if (primaryFailure == null) {
                    throw cleanupFailure;
                }
                primaryFailure.addSuppressed(cleanupFailure);
            }
        }
    }

    private static void rollback(
            final Connection connection,
            final Exception originalFailure) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            originalFailure.addSuppressed(rollbackFailure);
        }
    }

    private static SQLException cleanup(
            final Connection connection,
            final JdbcConnectionCleanup cleanup,
            final boolean originalAutoCommit) {
        SQLException failure = null;
        try {
            cleanup.cleanup(connection);
        } catch (SQLException exception) {
            failure = exception;
        }
        try {
            connection.setAutoCommit(originalAutoCommit);
        } catch (SQLException exception) {
            if (failure == null) {
                failure = exception;
            } else {
                failure.addSuppressed(exception);
            }
        }
        return failure;
    }

    private static final class Validation {

        private Validation() {
        }

        private static void requireArguments(
                final String failureMessage,
                final JdbcTransaction<?> transaction,
                final JdbcConnectionCleanup cleanup) {
            if (failureMessage == null || failureMessage.isBlank()) {
                throw new IllegalArgumentException("failureMessage must not be blank");
            }
            Objects.requireNonNull(transaction, "transaction");
            Objects.requireNonNull(cleanup, "cleanup");
        }
    }
}
