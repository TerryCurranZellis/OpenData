/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * SQL Server resource management and repository implementations owned by the core application module.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link DatabaseConnectionManager} &mdash; Compatibility facade used by repositories to borrow pooled connections.</li>
 * <li>{@link DatabaseHealthCheck} &mdash; Performs a lightweight SQL Server identity and database check.</li>
 * <li>{@link SqlServerRepository} &mdash; SQL Server implementation of the application database repository.</li>
 * <li>{@link SQLServerResource} &mdash; Singleton SQL Server resource backed by Apache Commons DBCP.</li>
 * <li>{@link UnavailableDatabaseResourceManager} &mdash; Dry-run marker resource that rejects persistent writes.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link DatabasePoolConfig} &mdash; Immutable Apache DBCP connection-pool settings.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link DatabaseRepository} &mdash; Defines core database persistence operations.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
package com.towermarsh.opendata.database;
