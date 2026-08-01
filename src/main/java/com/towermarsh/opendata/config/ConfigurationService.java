/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.util.List;
import java.util.Objects;

import com.towermarsh.opendata.cli.CommandLineArguments;

/**
 * Coordinates configuration loading and validation.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class ConfigurationService {

    /**
     * class loader
     */
    private final ConfigurationLoader loader;
    /**
     * list of valid configuratiosn
     */
    private final List<ConfigurationValidator> validators;

    /**
     * instantiate service
     */
    public ConfigurationService() {
        this(new ConfigurationLoader(), List.of(new StandardConfigurationValidator()));
    }

    /**
     * set up the service
     * @param loader loaders for service config
     * @param validators validators for service config
     */
    public ConfigurationService(
            final ConfigurationLoader loader,
            final List<ConfigurationValidator> validators) {

        this.loader = Objects.requireNonNull(loader, "loader");
        this.validators = List.copyOf(Objects.requireNonNull(validators, "validators"));
    }

    /**
     * Resolves and validates configuration for one application invocation.
     *
     * @param arguments parsed command-line arguments
     * @return resolved configuration
     */
    public ApplicationConfig resolve(final CommandLineArguments arguments) {
        final ApplicationConfig configuration = loader.load(arguments);
        validators.forEach(validator -> validator.validate(configuration));
        return configuration;
    }
}
