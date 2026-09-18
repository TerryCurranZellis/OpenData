/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Immutable plugin-definition, endpoint, credential, and configuration metadata.
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link PluginDefinition} &mdash; Structured plugin definition resolved from registration metadata.</li>
 * <li>{@link CredentialReference} &mdash; Reference to a credential stored outside the plugin definition.</li>
 * <li>{@link LinkDiscoveryDefinition} &mdash; Rules for selecting a downloadable link from an HTML page.</li>
 * <li>{@link PluginEndpointDefinition} &mdash; Complete definition of one source endpoint.</li>
 * <li>{@link PluginPropertyDefinition} &mdash; Typed plugin-specific configuration property.</li>
 * </ul>
 *
 * <h2>Enums</h2>
 * <ul>
 * <li>{@link AuthenticationType} &mdash; Supported endpoint authentication styles.</li>
 * <li>{@link CredentialLocation} &mdash; Request location where a credential is applied.</li>
 * <li>{@link DatasetFormat} &mdash; Supported dataset formats.</li>
 * <li>{@link DownloadStrategyType} &mdash; Download strategy categories available to plugins.</li>
 * <li>{@link EndpointType} &mdash; Semantic purpose of a configured endpoint.</li>
 * <li>{@link HttpMethod} &mdash; Supported HTTP request methods.</li>
 * <li>{@link PluginPropertyType} &mdash; Declared type of a plugin property value.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
package com.towermarsh.opendata.config.model;
