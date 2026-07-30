/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Parsed external override values, split into application and per-plugin
 * scopes.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class OverrideConfiguration {

    private final Map<String, String> values;

    /**
     * Creates an immutable override configuration.
     *
     * @param values raw normalised override values
     */
    private OverrideConfiguration(final Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    /**
     * Loads external override properties when a file was supplied.
     *
     * @param file optional path to the override file
     * @return parsed override configuration
     */
    public static OverrideConfiguration load(final Optional<Path> file) {
        Objects.requireNonNull(file, "file");
        if (file.isEmpty()) {
            return new OverrideConfiguration(Map.of());
        }
        final var path = file.get().toAbsolutePath().normalize();
        if (!Files.isRegularFile(path)) {
            throw new OpenDataConfigurationException("Override file does not exist: " + path);
        }
        try (var input = Files.newInputStream(path); var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            final var properties = new Properties();
            properties.load(reader);
            final Map<String, String> result = new LinkedHashMap<>();
            properties.stringPropertyNames().forEach(name -> result.put(normalise(name), properties.getProperty(name).trim()));
            return new OverrideConfiguration(result);
        } catch (IOException exception) {
            throw new OpenDataConfigurationException("Unable to read override file: " + path, exception);
        }
    }

    /**
     * Returns application-level override values without the `application.`
     * prefix.
     *
     * @return application override values
     */
    public Map<String, String> applicationValues() {
        return scoped("application.");
    }

    /**
     * Returns override values for a selected plugin.
     *
     * @param pluginId selected plugin identifier
     * @param multiPluginRun whether more than one plugin is executing
     * @return plugin override values without the `plugin.id.` prefix
     */
    public Map<String, String> pluginValues(final String pluginId, final boolean multiPluginRun) {
        final var prefix = "plugin." + normalise(pluginId) + ".";
        final Map<String, String> result = new LinkedHashMap<>(scoped(prefix));
        if (!multiPluginRun) {
            values.forEach((var key, var value) -> {
                if (!key.startsWith("application.") && !key.startsWith("plugin.")) {
                    result.put(key, value);
                }
            });
        } else {
            final var hasUnscoped = values.keySet().stream()
                    .anyMatch(key -> !key.startsWith("application.") && !key.startsWith("plugin."));
            if (hasUnscoped) {
                throw new OpenDataConfigurationException(
                        "A multi-plugin override file may only contain application.<key> and plugin.<id>.<key> entries.");
            }
        }
        return Map.copyOf(result);
    }

    /**
     * Returns override values whose keys begin with the supplied prefix.
     *
     * @param prefix key prefix to match
     * @return matching values with the prefix removed
     */
    private Map<String, String> scoped(final String prefix) {
        final Map<String, String> result = new LinkedHashMap<>();
        values.forEach((var key, var value) -> {
            if (key.startsWith(prefix)) {
                result.put(key.substring(prefix.length()), value);
            }
        });
        return result;
    }

    /**
     * Normalises override keys for case-insensitive lookup.
     *
     * @param value key to normalise
     * @return trimmed lower-case key
     */
    private static String normalise(final String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
