/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.time.Duration;

import com.towermarsh.opendata.exception.ConfigurationException;

/**
 * Performs framework-level validation common to every dataset plugin.
 *
 * @author Terry Curran
 * @version 2.1
 */
public final class StandardConfigurationValidator implements ConfigurationValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final ApplicationConfig configuration) {
        final var bootstrap = configuration.bootstrap();
        final var properties = new ApplicationPropertyValues(bootstrap.values());
        try {
            if (properties.contains("http.connect-timeout-seconds")
                    && properties.integer("http.connect-timeout-seconds", 1) <= 0) {
                throw new ConfigurationException("http.connect-timeout-seconds must be greater than zero.");
            }

            if (properties.contains("http.request-timeout-seconds")
                    && properties.integer("http.request-timeout-seconds", 1) <= 0) {
                throw new ConfigurationException("http.request-timeout-seconds must be greater than zero.");
            }

            final var workingDirectory = bootstrap.workingDirectory();
            if (workingDirectory.toString().isBlank()) {
                throw new ConfigurationException("application.working-directory must not be blank.");
            }

            if (properties.contains("pipeline.lock-timeout")) {
                properties.duration("pipeline.lock-timeout", Duration.ZERO);
            }
        } catch (OpenDataConfigurationException exception) {
            throw new ConfigurationException(exception.getMessage(), exception);
        }
    }
}
