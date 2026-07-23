package com.towermarsh.opendata.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Phase 1 registry backed by an explicit classpath plugin index.
 *
 * <p>An index is used instead of scanning a resource directory because
 * directory scanning is not portable once the application is packaged in a
 * JAR. The index resource is {@code config/plugins/index.properties}.</p>
 */
public final class ClasspathPluginRegistry implements PluginRegistry {

    private static final String INDEX_RESOURCE =
            "config/plugins/index.properties";
    private static final String PLUGIN_RESOURCE_PATTERN =
            "config/plugins/%s.properties";

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

    @Override
    public List<PluginDescriptor> list() {
        return plugins.values().stream()
                .sorted(Comparator.comparing(PluginDescriptor::id))
                .toList();
    }

    @Override
    public Optional<PluginDescriptor> find(final String pluginId) {
        return Optional.ofNullable(plugins.get(normaliseId(pluginId)));
    }

    @Override
    public PluginDescriptor requireEnabled(final String pluginId) {
        final PluginDescriptor descriptor = find(pluginId)
                .orElseThrow(() -> new PluginRegistryException(
                        "Plugin is not installed: " + pluginId));

        if (!descriptor.enabled()) {
            throw new PluginRegistryException(
                    "Plugin is installed but disabled: " + descriptor.id());
        }

        return descriptor;
    }

    private static Map<String, PluginDescriptor> loadPlugins(
            final ClassLoader classLoader) {

        final Properties index = loadRequiredProperties(
                classLoader,
                INDEX_RESOURCE);

        final String configuredIds = required(index, "plugins");
        final Map<String, PluginDescriptor> result = new LinkedHashMap<>();

        Arrays.stream(configuredIds.split(","))
                .map(ClasspathPluginRegistry::normaliseId)
                .filter(id -> !id.isBlank())
                .forEach(id -> {
                    final String resource =
                            PLUGIN_RESOURCE_PATTERN.formatted(id);
                    final PluginDescriptor descriptor =
                            readDescriptor(
                                    id,
                                    loadRequiredProperties(
                                            classLoader,
                                            resource),
                                    resource);

                    final PluginDescriptor existing =
                            result.putIfAbsent(id, descriptor);
                    if (existing != null) {
                        throw new PluginRegistryException(
                                "Duplicate plugin id in registry index: " + id);
                    }
                });

        return Map.copyOf(result);
    }

    private static PluginDescriptor readDescriptor(
            final String indexedId,
            final Properties properties,
            final String resource) {

        final String declaredId =
                normaliseId(required(properties, "plugin.id"));

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

    private static Properties loadRequiredProperties(
            final ClassLoader classLoader,
            final String resourceName) {

        try (InputStream input =
                     classLoader.getResourceAsStream(resourceName)) {

            if (input == null) {
                throw new PluginRegistryException(
                        "Plugin registry resource was not found: "
                                + resourceName);
            }

            final Properties properties = new Properties();
            properties.load(new InputStreamReader(
                    input,
                    StandardCharsets.UTF_8));
            return properties;
        } catch (IOException exception) {
            throw new PluginRegistryException(
                    "Unable to read plugin registry resource: "
                            + resourceName,
                    exception);
        }
    }

    private static String required(
            final Properties properties,
            final String key) {

        return Optional.ofNullable(properties.getProperty(key))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new PluginRegistryException(
                        "Required registry property is missing: " + key));
    }

    private static boolean booleanValue(
            final Properties properties,
            final String key,
            final boolean defaultValue) {

        final String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> throw new PluginRegistryException(
                    "Invalid boolean value for '%s': %s"
                            .formatted(key, value));
        };
    }

    private static int integerValue(
            final Properties properties,
            final String key,
            final int defaultValue) {

        final String value = properties.getProperty(key);
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

    private static String normaliseId(final String pluginId) {
        Objects.requireNonNull(pluginId, "pluginId");
        final String result =
                pluginId.trim().toLowerCase(Locale.ROOT);

        if (result.isEmpty()) {
            throw new PluginRegistryException(
                    "Plugin id must not be blank.");
        }
        return result;
    }
}
