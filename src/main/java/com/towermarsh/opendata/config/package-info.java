/**
 * Bootstrap loading, plugin properties parsing and configuration validation.
 *
 * <p>Phase 1 uses a properties file for each plugin but immediately converts it
 * into structured immutable Java records. Plugins must consume
 * {@link com.towermarsh.opendata.config.ApplicationConfig} and
 * {@link com.towermarsh.opendata.config.model.PluginDefinition}; they should
 * not read raw {@link java.util.Properties} instances.</p>
 *
 * <p>This boundary permits a later database and JSON implementation without
 * changing plugin interfaces.</p>
 */
package com.towermarsh.opendata.config;
