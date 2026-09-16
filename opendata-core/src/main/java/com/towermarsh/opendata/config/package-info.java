/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Immutable application, execution, logging, database-pool, and override configuration.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link ApplicationBootstrapPropertiesLoader} &mdash; Loads and stores the minimal bootstrap application properties file.</li>
 * <li>{@link ApplicationConfigurationService} &mdash; Creates the Phase 1 {@link ApplicationConfig}.</li>
 * <li>{@link ApplicationPropertyValues} &mdash; Typed, case-insensitive access to application configuration properties.</li>
 * <li>{@link BootstrapConfigurationLoader} &mdash; Loads application bootstrap configuration from the classpath.</li>
 * <li>{@link ClasspathConfigurationPropertiesSource} &mdash; Loads configuration properties from packaged classpath resources.</li>
 * <li>{@link ConfigurationLoader} &mdash; Loads and merges framework, application and packaged plugin properties.</li>
 * <li>{@link ConfigurationRegistrationService} &mdash; Registers application configuration and validated plugin definitions in SQL Server, then updates the bootstrap file for database-backed operation.</li>
 * <li>{@link ConfigurationService} &mdash; Coordinates configuration loading and validation.</li>
 * <li>{@link JdbcConfigurationPropertiesSource} &mdash; Loads and stores configuration properties in SQL Server.</li>
 * <li>{@link OpenDataConfigurationException} &mdash; Raised when application, database, execution, or override configuration is invalid.</li>
 * <li>{@link PluginConfigurationDirectoryScanner} &mdash; Locates plugin definition property files in OpenData configuration folders.</li>
 * <li>{@link PluginDefinitionException} &mdash; Thrown when a plugin properties file cannot be converted into a valid structured plugin definition.</li>
 * <li>{@link PluginDefinitionValidator} &mdash; Validates structural and cross-reference rules for plugin definitions.</li>
 * <li>{@link PluginRegistrationResolver} &mdash; Resolves validated plugin registrations from packaged or external properties.</li>
 * <li>{@link PropertiesFileConfigurationPropertiesSource} &mdash; Reads one plugin definition from an external UTF-8 properties file.</li>
 * <li>{@link PropertiesPluginDefinitionLoader} &mdash; Parses Phase 1 plugin definitions from classpath properties files.</li>
 * <li>{@link RsaConfigurationPasswordCipher} &mdash; Encrypts bootstrap passwords with an RSA public certificate and decrypts them with the matching private PKCS#12 certificate store.</li>
 * <li>{@link StandardConfigurationValidator} &mdash; Performs framework-level validation common to every dataset plugin.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link ApplicationBootstrapProperties} &mdash; Minimal bootstrap properties required before database-backed configuration can be loaded.</li>
 * <li>{@link ApplicationConfig} &mdash; Immutable configuration for one application execution.</li>
 * <li>{@link ApplicationRuntimeConfiguration} &mdash; Application-level settings loaded before plugin execution.</li>
 * <li>{@link DatabasePoolConfiguration} &mdash; SQL Server and Apache DBCP pool settings.</li>
 * <li>{@link ExecutionConfiguration} &mdash; Bounded plugin executor settings.</li>
 * <li>{@link LoggingConfiguration} &mdash; java.util.logging file-handler settings.</li>
 * <li>{@link PluginRegistration} &mdash; Validated plugin metadata and the complete property set to persist.</li>
 * <li>{@link ResolvedConfigurationValue} &mdash; A configuration value together with its source.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link ConfigurationPasswordCipher} &mdash; Encrypts and decrypts configuration secrets stored in properties.</li>
 * <li>{@link ConfigurationPropertiesSource} &mdash; Loads application and plugin property sets from one backing store.</li>
 * <li>{@link ConfigurationValidator} &mdash; Validates resolved application and plugin configuration.</li>
 * <li>{@link PluginDefinitionLoader} &mdash; Loads a structured plugin definition from a storage-specific representation.</li>
 * </ul>
 *
 * <h2>Enums</h2>
 * <ul>
 * <li>{@link ConfigurationSource} &mdash; Records where a resolved property value originated.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.config;
