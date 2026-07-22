package com.towermarsh.opendata.config;

import java.util.List;
import java.util.Objects;

import com.towermarsh.opendata.cli.CommandLineArguments;

/**
 * Coordinates configuration loading and validation.
 */
public final class ConfigurationService {

    private final ConfigurationLoader loader;
    private final List<ConfigurationValidator> validators;

    public ConfigurationService() {
        this(new ConfigurationLoader(), List.of(new StandardConfigurationValidator()));
    }

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
