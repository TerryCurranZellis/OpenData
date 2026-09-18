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
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link DatabaseResourceManager} &mdash; Contract for borrowing, closing, and monitoring pooled JDBC resources.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
package com.towermarsh.opendata.database;
