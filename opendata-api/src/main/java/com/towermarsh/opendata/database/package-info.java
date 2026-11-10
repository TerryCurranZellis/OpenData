/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Shared database resource contracts exposed to plugins and reusable infrastructure.
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link DatabasePoolSnapshot} &mdash; Point-in-time pooled-connection utilisation values.</li>
 * </ul>
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link DatabaseException} &mdash; Shared unchecked database failure.</li>
 * <li>{@link DatabaseAccessException} &mdash; SQL persistence failure wrapper.</li>
 * <li>{@link UnavailableDatabaseResourceManager} &mdash; Dry-run connection guard.</li>
 * <li>{@link SqlScriptExecutor} &mdash; SQL Server GO-batch script runner.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link DatabaseResourceManager} &mdash; Contract for borrowing, closing, and monitoring pooled JDBC resources.</li>
 * </ul>
 *
 * @author Terry Curran
 * @since 3.1.0
 * @version 4.1.0
 */
package com.towermarsh.opendata.database;
