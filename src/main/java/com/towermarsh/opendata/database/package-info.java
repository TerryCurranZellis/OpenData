/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * SQL Server pooled resource management and database exceptions.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link DatabaseAccessException} &mdash; Unchecked wrapper for database bootstrap and persistence failures.</li>
 * <li>{@link DatabaseConnectionManager} &mdash; Compatibility facade used by repositories to borrow pooled connections.</li>
 * <li>{@link DatabaseException} &mdash; Unchecked exception signalling an irrecoverable database operation failure.</li>
 * <li>{@link DatabaseHealthCheck} &mdash; Performs a lightweight SQL Server identity and database check.</li>
 * <li>{@link SqlServerRepository} &mdash; SQL Server implementation of the database repository.</li>
 * <li>{@link SQLServerResource} &mdash; Singleton SQL Server resource backed by Apache Commons DBCP.</li>
 * <li>{@link UnavailableDatabaseResourceManager} &mdash; Database marker used by a dry run; any attempted write is treated as a defect.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link DatabasePoolConfig} &mdash; Immutable Apache DBCP connection-pool settings.</li>
 * <li>{@link DatabasePoolSnapshot} &mdash; Point-in-time connection-pool utilisation values.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link DatabaseRepository} &mdash; Defines database persistence operations.</li>
 * <li>{@link DatabaseResourceManager} &mdash; Provides pooled JDBC resources to repositories.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.database;
