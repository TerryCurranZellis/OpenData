/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Reads one plugin definition from an external UTF-8 properties file.
 *
 * <p>The file uses the same unprefixed format as the packaged
 * {@code config/plugins/<id>.properties} resources.</p>
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class PropertiesFileConfigurationPropertiesSource
        implements ConfigurationPropertiesSource {

    private final Path file;

    /**
     * Creates a file-backed plugin configuration source.
     *
     * @param file plugin definition properties file
     */
    public PropertiesFileConfigurationPropertiesSource(final Path file) {
        this.file = Objects.requireNonNull(file, "file").toAbsolutePath().normalize();
    }

    @Override
    public Map<String, String> loadApplicationProperties() {
        return Map.of();
    }

    @Override
    public Map<String, String> loadPluginProperties(final String pluginId) {
        if (!Files.isRegularFile(file)) {
            throw new PluginDefinitionException("Plugin definition file was not found: " + file);
        }
        final Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException exception) {
            throw new OpenDataConfigurationException(
                    "Unable to read plugin definition file: " + file,
                    exception);
        }
        final Map<String, String> values = new LinkedHashMap<>();
        properties.stringPropertyNames().stream()
                .sorted()
                .forEach(name -> values.put(
                normalise(name),
                Objects.toString(properties.getProperty(name), "").trim()));
        return Map.copyOf(values);
    }

    private static String normalise(final String value) {
        return Objects.requireNonNull(value, "value")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
