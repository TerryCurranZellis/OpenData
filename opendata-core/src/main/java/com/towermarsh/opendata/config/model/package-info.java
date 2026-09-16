/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Immutable Java record types and enums representing structured application and plugin configuration.
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link BootstrapConfig} &mdash; Application settings required before a plugin definition can be loaded.</li>
 * <li>{@link CredentialReference} &mdash; Reference to a secret held outside the plugin definition.</li>
 * <li>{@link LinkDiscoveryDefinition} &mdash; Rules for finding a downloadable file link on an HTML landing page.</li>
 * <li>{@link PluginDefinition} &mdash; Structured Phase 1 definition parsed from a plugin properties file.</li>
 * <li>{@link PluginEndpointDefinition} &mdash; Complete definition of a source endpoint.</li>
 * <li>{@link PluginPropertyDefinition} &mdash; Typed plugin-specific configuration property.</li>
 * </ul>
 *
 * <h2>Enums</h2>
 * <ul>
 * <li>{@link AuthenticationType} &mdash; Authentication mechanisms supported by endpoint definitions.</li>
 * <li>{@link CredentialLocation} &mdash; Location in which a credential is applied to a request.</li>
 * <li>{@link DatasetFormat} &mdash; Data formats understood by the OpenData Framework.</li>
 * <li>{@link DownloadStrategyType} &mdash; Strategy used to obtain the dataset content.</li>
 * <li>{@link EndpointType} &mdash; Purpose and retrieval behaviour of a configured endpoint.</li>
 * <li>{@link HttpMethod} &mdash; Supported HTTP request methods.</li>
 * <li>{@link PluginPropertyType} &mdash; Declared type of a plugin-specific configuration property.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.config.model;
