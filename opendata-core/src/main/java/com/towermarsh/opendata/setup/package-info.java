/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * First-run installation services for SQL Server provisioning, AES credential setup and disabled plugin registration.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link InitialisationService} &mdash; Coordinates schema setup, plugin registration and bootstrap configuration.</li>
 * <li>{@link SqlServerInitialiser} &mdash; Creates SQL Server objects and application login.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link InitialisationRequest} &mdash; Validated first-run database and key-file settings.</li>
 * </ul>
 *
 * @author Terry Curran
 * @since 4.0.0
 * @version 4.1.0
 */
package com.towermarsh.opendata.setup;
