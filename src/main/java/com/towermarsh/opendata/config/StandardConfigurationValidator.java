package com.towermarsh.opendata.config;

import java.time.Duration;

import com.towermarsh.opendata.config.model.BootstrapConfig;
import com.towermarsh.opendata.exception.ConfigurationException;

/**
 * Performs framework-level validation common to every dataset plugin.
 */
public final class StandardConfigurationValidator implements ConfigurationValidator {

    /**
     * validate the configuration
     *
     * @param configuration configuration to validate
     */
    @Override
    public void validate(final ApplicationConfig configuration) {
        final BootstrapConfig bootstrap = configuration.bootstrap();

        final var connectTimeoutStr = bootstrap.values().get("http.connect-timeout-seconds");
        if (connectTimeoutStr != null) {
            try {
                final var connectTimeoutSeconds = Integer.parseInt(connectTimeoutStr.trim());
                if (connectTimeoutSeconds <= 0) {
                    throw new ConfigurationException(
                            "http.connect-timeout-seconds must be greater than zero.");
                }
            } catch (NumberFormatException exception) {
                throw new ConfigurationException(
                        "Invalid value for http.connect-timeout-seconds: " + connectTimeoutStr,
                        exception);
            }
        }

        final var requestTimeoutStr = bootstrap.values().get("http.request-timeout-seconds");
        if (requestTimeoutStr != null) {
            try {
                final var requestTimeoutSeconds = Integer.parseInt(requestTimeoutStr.trim());
                if (requestTimeoutSeconds <= 0) {
                    throw new ConfigurationException(
                            "http.request-timeout-seconds must be greater than zero.");
                }
            } catch (NumberFormatException exception) {
                throw new ConfigurationException(
                        "Invalid value for http.request-timeout-seconds: " + requestTimeoutStr,
                        exception);
            }
        }

        final var workingDirectory = bootstrap.workingDirectory();
        if (workingDirectory.toString().isBlank()) {
            throw new ConfigurationException("application.working-directory must not be blank.");
        }

        final var lockTimeoutStr = bootstrap.values().get("pipeline.lock-timeout");
        if (lockTimeoutStr != null) {
            try {
                Duration.parse(lockTimeoutStr.trim());
            } catch (java.time.format.DateTimeParseException exception) {
                throw new ConfigurationException(
                        "Invalid duration for pipeline.lock-timeout: " + lockTimeoutStr,
                        exception);
            }
        }
    }
}
