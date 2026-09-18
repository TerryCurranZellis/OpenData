/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Core application exception hierarchy layered on the shared base exceptions.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link ConfigurationException} &mdash; Thrown when bootstrap or plugin configuration cannot be loaded or validated.</li>
 * <li>{@link DiscoveryException} &mdash; Indicates that a dataset link could not be discovered or selected safely.</li>
 * <li>{@link ImportException} &mdash; Indicates a data import failure.</li>
 * <li>{@link PluginException} &mdash; Indicates a plugin stage failure after boundary translation.</li>
 * <li>{@link ValidationException} &mdash; Indicates that imported data failed validation.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
package com.towermarsh.opendata.exception;
