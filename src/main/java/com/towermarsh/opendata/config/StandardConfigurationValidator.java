package com.towermarsh.opendata.config;

import java.nio.file.Path;
import java.time.Duration;

import com.towermarsh.opendata.exception.ConfigurationException;

/**
 * Performs framework-level validation common to every dataset plugin.
 */
public final class StandardConfigurationValidator implements ConfigurationValidator {

    @Override
    public void validate(final ApplicationConfig configuration) {
        final String configuredPlugin = configuration.require("plugin.id");
        if (!configuration.pluginId().equalsIgnoreCase(configuredPlugin)) {
            throw new ConfigurationException(
                    "Selected plugin '%s' does not match configuration plugin.id '%s'."
                            .formatted(configuration.pluginId(), configuredPlugin));
        }

        final int connectTimeoutSeconds = configuration.getInt("http.connect-timeout-seconds", 30);
        final int requestTimeoutSeconds = configuration.getInt("http.request-timeout-seconds", 120);

        if (connectTimeoutSeconds <= 0) {
            throw new ConfigurationException("http.connect-timeout-seconds must be greater than zero.");
        }

        if (requestTimeoutSeconds <= 0) {
            throw new ConfigurationException("http.request-timeout-seconds must be greater than zero.");
        }

        final Path workingDirectory = configuration.getPath(
                "application.working-directory",
                Path.of("data", "work"));

        if (workingDirectory.toString().isBlank()) {
            throw new ConfigurationException("application.working-directory must not be blank.");
        }

        configuration.getDuration("pipeline.lock-timeout", Duration.ofSeconds(30));
    }
}
