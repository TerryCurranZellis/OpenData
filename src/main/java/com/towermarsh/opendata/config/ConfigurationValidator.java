package com.towermarsh.opendata.config;

/**
 * Validates resolved application and plugin configuration.
 */
@FunctionalInterface
public interface ConfigurationValidator {

    /**
     * Validates configuration and throws a ConfigurationException when invalid.
     *
     * @param configuration resolved configuration
     */
    void validate(ApplicationConfig configuration);
}
