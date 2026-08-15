/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Application logging configuration and contextual formatting.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link ContextualLogFormatter} &mdash; Thread-safe JUL formatter containing thread, plugin, and run context.</li>
 * <li>{@link LoggingManager} &mdash; Central java.util.logging configuration.</li>
 * <li>{@link PluginLogContext} &mdash; Per-thread plugin and run identifiers added to every log line.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.logging;
