/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import com.towermarsh.opendata.config.model.BootstrapConfig;

/**
 * Loads application bootstrap configuration from the classpath.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class BootstrapConfigurationLoader {

    /**
     * location of the resource file
     */
    private static final String RESOURCE
            = "config/application.properties";

    private final ClassLoader classLoader;

    /**
     * Creates a loader that uses the thread context class loader.
     */
    public BootstrapConfigurationLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Creates a loader using the supplied class loader.
     *
     * @param classLoader class loader containing the bootstrap resource
     */
    public BootstrapConfigurationLoader(final ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    /**
     * Loads bootstrap configuration from the packaged application properties
     * resource.
     *
     * @return bootstrap configuration
     */
    public BootstrapConfig load() {
        try (var input = classLoader.getResourceAsStream(RESOURCE)) {
            if (input == null) {
                throw new PluginDefinitionException(
                        "Bootstrap resource was not found: " + RESOURCE);
            }
            final var properties = new Properties();
            properties.load(new InputStreamReader(
                    input,
                    StandardCharsets.UTF_8));
            final Map<String, String> values = new LinkedHashMap<>();
            properties.stringPropertyNames().forEach(
                    name -> values.put(name, properties.getProperty(name).trim()));
            return new BootstrapConfig(
                    require(values, "application.name"),
                    require(values, "application.environment"),
                    Path.of(require(values, "application.working-directory")),
                    Path.of(require(values, "application.archive-directory")),
                    Path.of(require(values, "application.failed-directory")),
                    values);
        } catch (IOException exception) {
            throw new PluginDefinitionException(
                    "Unable to load bootstrap configuration.",
                    exception);
        }
    }

    /**
     * Returns a required bootstrap property.
     *
     * @param values bootstrap property values
     * @param key property key to resolve
     * @return trimmed property value
     */
    private static String require(
            final Map<String, String> values,
            final String key) {

        final var value = values.get(key);
        if (value == null || value.isBlank()) {
            throw new PluginDefinitionException(
                    "Required bootstrap property is missing: " + key);
        }
        return value;
    }
}
