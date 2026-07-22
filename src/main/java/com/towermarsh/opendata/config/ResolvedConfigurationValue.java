package com.towermarsh.opendata.config;

/**
 * A configuration value together with its source.
 *
 * @param value resolved text value
 * @param source source that supplied the value
 */
public record ResolvedConfigurationValue(
        String value,
        ConfigurationSource source) {
}
