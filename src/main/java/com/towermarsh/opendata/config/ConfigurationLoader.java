/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import com.towermarsh.opendata.cli.CommandLineArguments;
import com.towermarsh.opendata.config.model.BootstrapConfig;
import com.towermarsh.opendata.exception.ConfigurationException;
import java.io.InputStreamReader;

/**
 * Loads and merges framework, application and packaged plugin properties.
 *
 * <p>
 * Environment variables are deliberately not used. External plugin files are
 * handled only by the registration command and are not invocation overrides.</p>
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class ConfigurationLoader {

    /**
     * Application resources file
     */
    private static final String APPLICATION_RESOURCE = "config/application.properties";

    /**
     * plugin loaded locations
     */
    private static final String PLUGIN_RESOURCE_PATTERN = "config/plugins/%s.properties";

    /**
     * load classes
     */
    private final ClassLoader classLoader;
    /**
     * create a list of default settings
     */
    private final Map<String, String> builtInDefaults;

    /**
     * Creates a configuration loader using the thread context class loader.
     */
    public ConfigurationLoader() {
        this(Thread.currentThread().getContextClassLoader(), standardDefaults());
    }

    /**
     * Creates the configuration settings and load
     *
     * @param classLoader
     * @param builtInDefaults
     */
    ConfigurationLoader(
            final ClassLoader classLoader,
            final Map<String, String> builtInDefaults) {

        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
        this.builtInDefaults = Map.copyOf(Objects.requireNonNull(builtInDefaults, "builtInDefaults"));
    }

    /**
     * Loads the complete configuration for the parsed command line.
     *
     * @param arguments parsed command-line arguments
     * @return immutable resolved configuration
     * @throws com.towermarsh.opendata.exception.ConfigurationException if the
     * resource cannot be loaded
     */
    public ApplicationConfig load(final CommandLineArguments arguments) throws ConfigurationException {
        Objects.requireNonNull(arguments, "arguments");

        final var pluginId = arguments.pluginIds().stream().findFirst()
                .orElseThrow(() -> new ConfigurationException(
                "A named plugin is required before configuration can be loaded."));

        final Map<String, ResolvedConfigurationValue> merged = new LinkedHashMap<>();
        merge(merged, builtInDefaults, ConfigurationSource.BUILT_IN_DEFAULT);
        merge(merged, loadOptionalClasspathProperties(APPLICATION_RESOURCE),
                ConfigurationSource.APPLICATION_CLASSPATH);

        final var pluginResource = PLUGIN_RESOURCE_PATTERN.formatted(pluginId);
        final var pluginDefaults = loadRequiredClasspathProperties(pluginResource);
        merge(merged, pluginDefaults, ConfigurationSource.PLUGIN_CLASSPATH);

        final Map<String, String> values = new LinkedHashMap<>();
        merged.forEach((key, resolved) -> values.put(key, resolved.value()));

        final var bootstrap = buildBootstrapConfig(values);
        final var plugin = new PropertiesPluginDefinitionLoader(
                new ClasspathConfigurationPropertiesSource(classLoader)).load(pluginId, values);

        return new ApplicationConfig(
                bootstrap,
                plugin,
                Map.of(),
                arguments.dryRun(),
                arguments.verbose());
    }

    /**
     * Builds bootstrap configuration from merged key-value pairs.
     *
     * @param values merged configuration values
     * @return bootstrap configuration
     */
    private static BootstrapConfig buildBootstrapConfig(final Map<String, String> values) {
        return new BootstrapConfig(
                values.getOrDefault("application.name", "OpenData"),
                values.getOrDefault("application.environment", "production"),
                Path.of(values.getOrDefault("application.working-directory", "data/work")),
                Path.of(values.getOrDefault("application.archive-directory", "data/archive")),
                Path.of(values.getOrDefault("application.failed-directory", "data/failed")),
                values);
    }

    /**
     * Loads an optional classpath properties resource.
     *
     * @param resourceName classpath resource name
     * @return loaded properties or an empty map when the resource is absent
     * @throws ConfigurationException if the resource cannot be closed after
     * reading
     */
    private Map<String, String> loadOptionalClasspathProperties(final String resourceName) throws ConfigurationException {
        try (var input = classLoader.getResourceAsStream(resourceName)) {
            if (input == null) {
                return Map.of();
            }
            return readProperties(input, "classpath:" + resourceName);
        } catch (IOException exception) {
            throw new ConfigurationException(
                    "Unable to close classpath configuration resource: " + resourceName,
                    exception);
        }
    }

    /**
     * Loads a required classpath properties resource.
     *
     * @param resourceName classpath resource name
     * @return loaded properties
     * @throws ConfigurationException if the resource is missing or cannot be
     * closed
     */
    private Map<String, String> loadRequiredClasspathProperties(final String resourceName) throws ConfigurationException {
        try (var input = classLoader.getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new ConfigurationException(
                        "Plugin configuration resource was not found: classpath:" + resourceName);
            }
            return readProperties(input, "classpath:" + resourceName);
        } catch (IOException exception) {
            throw new ConfigurationException(
                    "Unable to close plugin configuration resource: " + resourceName,
                    exception);
        }
    }

    /**
     * Reads UTF-8 Java properties into a normalised string map.
     *
     * @param input source input stream
     * @param sourceDescription source description for error reporting
     * @return normalised properties
     * @throws ConfigurationException if the properties cannot be parsed
     */
    private static Map<String, String> readProperties(
            final InputStream input,
            final String sourceDescription) throws ConfigurationException {

        final var properties = new Properties();
        try {
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new ConfigurationException(
                    "Unable to parse properties from " + sourceDescription,
                    exception);
        }

        final Map<String, String> result = new LinkedHashMap<>();
        properties.stringPropertyNames().forEach((var name) -> {
            final var key = normaliseKey(name);
            final var value = Optional.ofNullable(properties.getProperty(name))
                    .orElse("")
                    .trim();
            if (result.put(key, value) != null) {
                throw new ConfigurationException(
                        "Duplicate configuration property after key normalisation: " + key);
            }
        });
        return result;
    }

    /**
     * Merges configuration values into the resolved map using the supplied
     * source tag.
     *
     * @param target merge target keyed by normalised property name
     * @param source raw source values
     * @param sourceType source type recorded for each merged value
     */
    private static void merge(
            final Map<String, ResolvedConfigurationValue> target,
            final Map<String, String> source,
            final ConfigurationSource sourceType) {

        source.forEach((key, value)
                -> target.put(normaliseKey(key), new ResolvedConfigurationValue(value, sourceType)));
    }

    /**
     * Normalises a configuration property name for case-insensitive lookup.
     *
     * @param key property name to normalise
     * @return trimmed lower-case property name
     * @throws ConfigurationException if the property name is blank
     */
    private static String normaliseKey(final String key) throws ConfigurationException {
        Objects.requireNonNull(key, "key");
        final var normalised = key.trim().toLowerCase(Locale.ROOT);
        if (normalised.isEmpty()) {
            throw new ConfigurationException("Configuration property names must not be blank.");
        }
        return normalised;
    }

    /**
     * Returns built-in fallback configuration values.
     *
     * @return default configuration values
     */
    private static Map<String, String> standardDefaults() {
        final Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put("application.working-directory", "data/work");
        defaults.put("application.archive-directory", "data/archive");
        defaults.put("application.failed-directory", "data/failed");
        defaults.put("http.connect-timeout-seconds", "30");
        defaults.put("http.request-timeout-seconds", "120");
        defaults.put("http.follow-redirects", "true");
        defaults.put("pipeline.lock-timeout", "PT30S");
        defaults.put("database.batch-size", "1000");
        defaults.put("database.transactional", "true");
        return defaults;
    }
}
