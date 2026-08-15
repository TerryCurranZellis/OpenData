/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Application exception hierarchy.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link ConfigurationException} &mdash; Thrown when application or plugin configuration cannot be loaded, parsed or validated.</li>
 * <li>{@link DiscoveryException} &mdash; Indicates that a dataset link could not be discovered or selected safely.</li>
 * <li>{@link DownloadException} &mdash; Indicates an error downloading OpenData files.</li>
 * <li>{@link ImportException} &mdash; Indicates a data import failure.</li>
 * <li>{@link OpenDataException} &mdash; Base exception for all OpenData application errors.</li>
 * <li>{@link PluginException} &mdash; Exception raised when a plugin step fails.</li>
 * <li>{@link ValidationException} &mdash; Indicates that imported data failed validation.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.exception;
