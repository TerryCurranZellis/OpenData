/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Application bootstrap and aggregate execution status.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link OpenDataApplication} &mdash; Coordinates plugin administration, registry selection, configuration, pooled database access, and plugin execution.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link ApplicationInfo} &mdash; Supplies application identity and run-time metadata shared by CLI and GUI code.</li>
 * </ul>
 *
 * <h2>Enums</h2>
 * <ul>
 * <li>{@link ExecutionStatus} &mdash; Final status reported by one OpenData process invocation.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.app;
