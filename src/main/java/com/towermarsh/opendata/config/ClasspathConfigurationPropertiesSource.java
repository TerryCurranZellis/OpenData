/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Loads configuration properties from packaged classpath resources.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class ClasspathConfigurationPropertiesSource
        implements ConfigurationPropertiesSource {

    /**
     * Application resource name.
     */
    public static final String APPLICATION_RESOURCE = "config/application.properties";

    private static final String PLUGIN_RESOURCE_PATTERN = "config/plugins/%s.properties";

    private final ClassLoader classLoader;

    /**
     * Creates a source backed by the thread context class loader.
     */
    public ClasspathConfigurationPropertiesSource() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Creates a source backed by one class loader.
     *
     * @param classLoader class loader containing the resources
     */
    public ClasspathConfigurationPropertiesSource(final ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> loadApplicationProperties() {
        return loadRequiredResource(APPLICATION_RESOURCE, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> loadPluginProperties(final String pluginId) {
        final var resourceName = PLUGIN_RESOURCE_PATTERN.formatted(normalise(pluginId));
        return loadRequiredResource(resourceName, false);
    }

    /**
     * Reads one properties resource.
     *
     * @param resourceName resource name
     * @param allowMissing whether absence should return an empty map
     * @return normalised property values
     */
    private Map<String, String> loadRequiredResource(
            final String resourceName,
            final boolean allowMissing) {
        try (var input = classLoader.getResourceAsStream(resourceName)) {
            if (input == null) {
                if (allowMissing) {
                    return Map.of();
                }
                throw new PluginDefinitionException(
                        "Plugin properties resource was not found: " + resourceName);
            }
            final var properties = new Properties();
            try (var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
            return properties.stringPropertyNames().stream()
                    .collect(Collectors.toMap(
                            ClasspathConfigurationPropertiesSource::normalise,
                            name -> properties.getProperty(name).trim(),
                            (first, second) -> second,
                            LinkedHashMap::new));
        } catch (IOException exception) {
            throw new OpenDataConfigurationException(
                    "Unable to read configuration resource: " + resourceName,
                    exception);
        }
    }

    /**
     * Normalises one property key.
     *
     * @param value property key
     * @return trimmed lower-case property key
     */
    private static String normalise(final String value) {
        return Objects.requireNonNull(value, "value").trim().toLowerCase(Locale.ROOT);
    }
}
