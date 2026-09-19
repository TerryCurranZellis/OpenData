/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Bundled plugin registry, execution, auditing, factory, and provider
 * implementations.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link ClasspathPluginRegistry} &mdash; Packaged plugin catalogue backed by an explicit classpath index.</li>
 * <li>{@link JdbcPluginRegistry} &mdash; Persistent SQL Server plugin registry and administration service.</li>
 * <li>{@link JdbcPluginRunAudit} &mdash; SQL Server implementation of the generic plugin-run audit.</li>
 * <li>{@link NoOpPluginRunAudit} &mdash; Audit implementation for dry runs and tests.</li>
 * <li>{@link PluginExceptionHandler} &mdash; Converts plugin stage failures into the framework's standard plugin exception.</li>
 * <li>{@link PluginExecutionCoordinator} &mdash; Runs each selected plugin as an isolated task on a bounded executor.</li>
 * <li>{@link PluginRegistryException} &mdash; Indicates invalid or missing plugin registry metadata.</li>
 * <li>{@link PluginSelectionResolver} &mdash; Resolves named plugins or every enabled plugin from the authoritative registry.</li>
 * <li>{@link PluginThreadFactory} &mdash; Names non-daemon plugin worker threads for logs and diagnostics.</li>
 * <li>{@link ReflectionPluginFactory} &mdash; Creates configured plugin classes named in plugin properties.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link PluginExecutionSummary} &mdash; Aggregate result of one multi-plugin invocation.</li>
 * <li>{@link PluginRunResult} &mdash; Completed result for one selected plugin.</li>
 * <li>{@link ResolvedPlugin} &mdash; Installed plugin metadata paired with its invocation-specific definition.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link PluginFactory} &mdash; Creates a fresh plugin instance for one task.</li>
 * <li>{@link PluginRegistry} &mdash; Provides metadata for OpenData plugins available to a particular operation.</li>
 * <li>{@link PluginRunAudit} &mdash; Persists generic plugin-run lifecycle information.</li>
 * </ul>
 *
 * <h2>Enums</h2>
 * <ul>
 * <li>{@link PluginRunStatus} &mdash; Database and process status for one plugin task.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
package com.towermarsh.opendata.plugin;
