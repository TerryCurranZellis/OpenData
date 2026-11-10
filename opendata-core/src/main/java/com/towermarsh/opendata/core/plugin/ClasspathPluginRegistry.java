/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.plugin;

import com.towermarsh.opendata.plugin.PluginDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Packaged plugin catalogue backed by an explicit classpath index.
 *
 * <p>
 * Each plugin module contributes one {@code config/plugins/index.properties}
 * resource so directory scanning is not required once the application is
 * packaged in JARs.</p>
 *
 * @author Terry Curran
 * @since 2.0.0
 * @version 4.1.0
 */
public final class ClasspathPluginRegistry implements PluginRegistry {

    private static final String INDEX_RESOURCE
            = "config/plugins/index.properties";
    private static final String PLUGIN_RESOURCE_PATTERN
            = "config/plugins/%s.properties";

    private final Map<String, PluginDescriptor> plugins;

    /**
     * Loads the registry with the thread context class loader.
     */
    public ClasspathPluginRegistry() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Loads the registry with a specified class loader.
     *
     * @param classLoader class loader containing plugin resources
     */
    public ClasspathPluginRegistry(final ClassLoader classLoader) {
        Objects.requireNonNull(classLoader, "classLoader");
        this.plugins = loadPlugins(classLoader);
    }

    /**
     * {@inheritDoc}
     *
     * @return result of list
     */
    @Override
    public List<PluginDescriptor> list() {
        return plugins.values().stream()
                .sorted(Comparator.comparing(PluginDescriptor::id))
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * @param pluginId requested plugin id
     * @return result of find
     */
    @Override
    public Optional<PluginDescriptor> find(final String pluginId) {
        return Optional.ofNullable(plugins.get(normaliseId(pluginId)));
    }

    /**
     * {@inheritDoc}
     *
     * @param pluginId requested plugin id
     * @return result of require enabled
     */
    @Override
    public PluginDescriptor requireEnabled(final String pluginId) {
        var descriptor = find(pluginId)
                .orElseThrow(() -> new PluginRegistryException(
                "Packaged plugin definition was not found: " + pluginId));

        if (!descriptor.enabled()) {
            throw new PluginRegistryException(
                    "Packaged plugin definition is disabled: " + descriptor.id());
        }

        return descriptor;
    }

    /**
     * Returns load plugins.
     *
     * @param classLoader input class loader
     * @return result of load plugins
     */
    private static Map<String, PluginDescriptor> loadPlugins(
            ClassLoader classLoader) {
        var result = new LinkedHashMap<String, PluginDescriptor>();
        var indexes = loadRequiredIndexes(classLoader);

        for (var entry : indexes.entrySet()) {
            Arrays.stream(required(entry.getValue(), "plugins").split(","))
                    .map(ClasspathPluginRegistry::normaliseId)
                    .filter(id -> !id.isBlank())
                    .forEach(id -> addDescriptor(classLoader, result, id, entry.getKey()));
        }

        return Map.copyOf(result);
    }

    /**
     * Add descriptor.
     *
     * @param classLoader input class loader
     * @param result input result
     * @param id requested id
     * @param indexResource input index resource
     */
    private static void addDescriptor(
            ClassLoader classLoader,
            Map<String, PluginDescriptor> result,
            String id,
            String indexResource) {
        var resource = PLUGIN_RESOURCE_PATTERN.formatted(id);
        var descriptor = readDescriptor(
                id,
                loadRequiredProperties(classLoader, resource),
                resource);

        var existing = result.putIfAbsent(id, descriptor);
        if (existing != null) {
            throw new PluginRegistryException(
                    "Duplicate plugin id '%s' discovered via %s."
                            .formatted(id, indexResource));
        }
    }

    /**
     * Returns load required indexes.
     *
     * @param classLoader input class loader
     * @return result of load required indexes
     */
    private static Map<String, Properties> loadRequiredIndexes(final ClassLoader classLoader) {
        var indexes = new LinkedHashMap<String, Properties>();
        final Enumeration<java.net.URL> resources;
        try {
            resources = classLoader.getResources(INDEX_RESOURCE);
        } catch (IOException exception) {
            throw new PluginRegistryException(
                    "Unable to enumerate plugin registry resources: " + INDEX_RESOURCE,
                    exception);
        }

        while (resources.hasMoreElements()) {
            var resource = resources.nextElement();
            var resourceName = resource.toExternalForm();
            indexes.put(resourceName, loadRequiredProperties(classLoader, resourceName, resource));
        }

        if (indexes.isEmpty()) {
            throw new PluginRegistryException(
                    "Plugin registry resource was not found: " + INDEX_RESOURCE);
        }

        return Map.copyOf(indexes);
    }

    /**
     * Returns read descriptor.
     *
     * @param indexedId requested indexed id
     * @param properties input properties
     * @param resource input resource
     * @return result of read descriptor
     */
    private static PluginDescriptor readDescriptor(
            String indexedId,
            Properties properties,
            String resource) {

        var declaredId = normaliseId(required(properties, "plugin.id"));

        if (!indexedId.equals(declaredId)) {
            throw new PluginRegistryException(
                    "Plugin index id '%s' does not match plugin.id '%s' in %s."
                            .formatted(indexedId, declaredId, resource));
        }

        return new PluginDescriptor(
                declaredId,
                required(properties, "plugin.display-name"),
                properties.getProperty("plugin.description", ""),
                required(properties, "plugin.implementation-class"),
                booleanValue(properties, "plugin.enabled", true),
                integerValue(
                        properties,
                        "plugin.configuration-version",
                        1));
    }

    /**
     * Returns load required properties.
     *
     * @param classLoader input class loader
     * @param resourceName requested resource name
     * @return result of load required properties
     */
    private static Properties loadRequiredProperties(
            ClassLoader classLoader,
            String resourceName) {
        try (var input = classLoader.getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new PluginRegistryException(
                        "Plugin registry resource was not found: "
                        + resourceName);
            }
            var properties = new Properties();
            try (var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
            return properties;
        } catch (IOException exception) {
            throw new PluginRegistryException(
                    "Unable to read plugin registry resource: "
                    + resourceName,
                    exception);
        }
    }

    /**
     * Returns load required properties.
     *
     * @param classLoader input class loader
     * @param resourceName requested resource name
     * @param resource input resource
     * @return result of load required properties
     */
    private static Properties loadRequiredProperties(
            ClassLoader classLoader,
            String resourceName,
            java.net.URL resource) {
        try (var input = resource.openStream()) {
            var properties = new Properties();
            try (var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
            return properties;
        } catch (IOException exception) {
            throw new PluginRegistryException(
                    "Unable to read plugin registry resource: "
                    + resourceName,
                    exception);
        }
    }

    /**
     * Required.
     *
     * @param properties input properties
     * @param key input key
     * @return result of required
     */
    private static String required(
            Properties properties,
            String key) {

        return Optional.ofNullable(properties.getProperty(key))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new PluginRegistryException(
                "Required registry property is missing: " + key));
    }

    /**
     * Boolean value.
     *
     * @param properties input properties
     * @param key input key
     * @param defaultValue value of default value
     * @return result of boolean value
     */
    private static boolean booleanValue(
            Properties properties,
            String key,
            boolean defaultValue) {

        final String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" ->
                true;
            case "false", "no", "0", "off" ->
                false;
            default ->
                throw new PluginRegistryException(
                        "Invalid boolean value for '%s': %s"
                                .formatted(key, value));
        };
    }

    /**
     * Integer value.
     *
     * @param properties input properties
     * @param key input key
     * @param defaultValue value of default value
     * @return result of integer value
     */
    private static int integerValue(
            Properties properties,
            String key,
            int defaultValue) {

        var value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new PluginRegistryException(
                    "Invalid integer value for '%s': %s"
                            .formatted(key, value),
                    exception);
        }
    }

    /**
     * Normalise id.
     *
     * @param pluginId requested plugin id
     * @return result of normalise id
     */
    private static String normaliseId(final String pluginId) {
        Objects.requireNonNull(pluginId, "pluginId");
        var result
                = pluginId.trim().toLowerCase(Locale.ROOT);

        if (result.isEmpty()) {
            throw new PluginRegistryException(
                    "Plugin id must not be blank.");
        }
        return result;
    }
}
