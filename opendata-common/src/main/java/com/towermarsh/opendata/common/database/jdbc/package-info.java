/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Shared JDBC transaction, batch, and typed upsert infrastructure.
 *
 * <p>Plugin repositories retain their own SQL and schema knowledge while this
 * package supplies the common execution mechanics.</p>
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link JdbcBatchExecutor} &mdash; Executes generic batches with subtype records and supertype binders.</li>
 * <li>{@link JdbcTransactionTemplate} &mdash; Provides consistent JDBC transaction, rollback, and pooled-session cleanup.</li>
 * <li>{@link JdbcUpsertExecutor} &mdash; Runs generic record-by-record upserts with contravariant adapters.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link JdbcUpsertResult} &mdash; Summary of one generic JDBC upsert operation.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link JdbcConnectionCleanup} &mdash; Removes connection-scoped state before a pooled JDBC connection is returned.</li>
 * <li>{@link JdbcStatementBinder} &mdash; Binds one typed record to a prepared JDBC statement.</li>
 * <li>{@link JdbcTransaction} &mdash; Database work executed inside one JDBC transaction.</li>
 * <li>{@link JdbcUpsertAdapter} &mdash; Defines record-specific existence, insert, and update operations.</li>
 * </ul>
 *
 * @author Terry Curran
 * @since 3.0.0
 * @version 4.1.0
 */
package com.towermarsh.opendata.common.database.jdbc;
