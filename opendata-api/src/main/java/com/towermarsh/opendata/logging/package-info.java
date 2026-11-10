/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Plugin execution log correlation contracts shared across module boundaries.
 *
 * <h2>Classes</h2>
 * <ul><li>{@link com.towermarsh.opendata.logging.PluginLogContext} &mdash;
 * per-thread plugin and run context.</li></ul>
 * <h2>Records</h2>
 * <ul><li>{@link com.towermarsh.opendata.logging.PluginLogContext.Entry} &mdash;
 * immutable run identifiers.</li></ul>
 * <h2>Interfaces</h2>
 * <ul><li>{@link com.towermarsh.opendata.logging.PluginLogContext.Scope} &mdash;
 * closable context scope.</li></ul>
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
package com.towermarsh.opendata.logging;
