/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Shared plugin contracts and immutable execution metadata.
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link PluginDescriptor} &mdash; Immutable metadata describing one installed plugin.</li>
 * <li>{@link PluginExecutionContext} &mdash; Run-scoped plugin definition, descriptor, database access and clock values.</li>
 * <li>{@link PluginMetrics} &mdash; Row counts returned by one plugin execution.</li>
 * </ul>
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link PluginLogContext} &mdash; Per-thread contextual plugin logging scope.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link OpenDataPlugin} &mdash; Contract implemented by every executable OpenData plugin.</li>
 * <li>{@link PluginRegistrar} &mdash; Plugin-specific database schema registration contract.</li>
 * </ul>
 *
 * @author Terry Curran
 * @since 3.1.0
 * @version 4.1.0
 */
package com.towermarsh.opendata.plugin;
