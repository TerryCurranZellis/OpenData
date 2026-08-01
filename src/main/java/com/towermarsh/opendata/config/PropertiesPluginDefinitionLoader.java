/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.net.URI;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import com.towermarsh.opendata.config.model.AuthenticationType;
import com.towermarsh.opendata.config.model.CredentialLocation;
import com.towermarsh.opendata.config.model.CredentialReference;
import com.towermarsh.opendata.config.model.DatasetFormat;
import com.towermarsh.opendata.config.model.DownloadStrategyType;
import com.towermarsh.opendata.config.model.EndpointType;
import com.towermarsh.opendata.config.model.HttpMethod;
import com.towermarsh.opendata.config.model.LinkDiscoveryDefinition;
import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginEndpointDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyDefinition;
import com.towermarsh.opendata.config.model.PluginPropertyType;

/**
 * Parses Phase 1 plugin definitions from classpath properties files.
 *
 * <p>
 * The expected resource is {@code config/plugins/<plugin-id>.properties}.
 * Supplied overrides are merged after the classpath properties and therefore
 * take precedence.</p>
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class PropertiesPluginDefinitionLoader
        implements PluginDefinitionLoader {

    private final ConfigurationPropertiesSource source;

    /**
     * instantiate class
     */
    public PropertiesPluginDefinitionLoader() {
        this(new ClasspathConfigurationPropertiesSource());
    }

    /**
     * instantiate class
     *
     * @param source configuration property source
     */
    public PropertiesPluginDefinitionLoader(final ConfigurationPropertiesSource source) {
        this.source = Objects.requireNonNull(source, "source");
    }

    /**
     * Load the plugin definition
     *
     * @param pluginId which plugin
     * @param overrides plugin overrides
     * @return the plugin definition
     */
    @Override
    public PluginDefinition load(
            final String pluginId,
            final Map<String, String> overrides) {

        final var normalisedPluginId = normalise(pluginId);
        final var values = new LinkedHashMap<>(source.loadPluginProperties(normalisedPluginId));
        Objects.requireNonNull(overrides, "overrides")
                .forEach((key, value)
                        -> values.put(normalise(key), value == null ? "" : value.trim()));

        final var definition = parse(values);
        if (!definition.id().equals(normalisedPluginId)) {
            throw new PluginDefinitionException(
                    "Selected plugin '%s' does not match plugin.id '%s'."
                            .formatted(normalisedPluginId, definition.id()));
        }

        new PluginDefinitionValidator().validate(definition);
        return definition;
    }

    /**
     * Parse the plugin definition
     *
     * @param values map of keys and values
     * @return the plugin definition
     */
    PluginDefinition parse(final Map<String, String> values) {
        final var id = require(values, "plugin.id").toLowerCase(Locale.ROOT);
        final var endpoints = parseEndpoints(values);
        final var properties = parsePluginProperties(values);
        final var credentials = parseCredentials(values);

        return new PluginDefinition(
                id,
                require(values, "plugin.display-name"),
                values.getOrDefault("plugin.description", ""),
                require(values, "plugin.implementation-class"),
                getBoolean(values, "plugin.enabled", true),
                getInt(values, "plugin.configuration-version", 1),
                require(values, "dataset.id"),
                endpoints,
                properties,
                credentials);
    }

    /**
     * Parse plugin end points
     *
     * @param values map of values
     * @return list of plugin endpoints
     */
    private List<PluginEndpointDefinition> parseEndpoints(
            final Map<String, String> values) {

        final var names = extractNames(values, "endpoint.");
        final List<PluginEndpointDefinition> result = new ArrayList<>();

        for (var name : names) {
            final var prefix = "endpoint." + name + ".";
            final var discovery = parseLinkDiscovery(values, prefix);

            result.add(new PluginEndpointDefinition(
                    name,
                    enumValue(values, prefix + "type", EndpointType.class),
                    URI.create(require(values, prefix + "url")),
                    enumValue(values, prefix + "method", HttpMethod.class),
                    DatasetFormat.parse(require(values, prefix + "format")),
                    enumValue(values, prefix + "strategy", DownloadStrategyType.class),
                    getBoolean(values, prefix + "enabled", true),
                    getInt(values, prefix + "order", 0),
                    optional(values, prefix + "credential"),
                    subMap(values, prefix + "header."),
                    subMap(values, prefix + "query."),
                    discovery));
        }

        return result.stream()
                .sorted(Comparator.comparingInt(PluginEndpointDefinition::order)
                        .thenComparing(PluginEndpointDefinition::name))
                .toList();
    }

    /**
     * pase links
     *
     * @param values map of values
     * @param endpointPrefix endpoint
     * @return definition if it exists
     */
    private Optional<LinkDiscoveryDefinition> parseLinkDiscovery(
            final Map<String, String> values,
            final String endpointPrefix) {

        final var prefix = endpointPrefix + "link-discovery.";
        if (!values.containsKey(prefix + "css-selector")) {
            return Optional.empty();
        }

        return Optional.of(new LinkDiscoveryDefinition(
                require(values, prefix + "css-selector"),
                require(values, prefix + "href-pattern"),
                values.getOrDefault(prefix + "text-pattern", ""),
                getBoolean(values, prefix + "select-last", false)));
    }

    /**
     * parse plugins
     *
     * @param values map of key value pairs
     * @return map of plugin and properties
     */
    private Map<String, PluginPropertyDefinition> parsePluginProperties(
            final Map<String, String> values) {

        final var names = extractPropertyNames(values);
        final Map<String, PluginPropertyDefinition> result = new LinkedHashMap<>();

        names.forEach((var name) -> {
            final var prefix = "property." + name + ".";
            final var propertyType = enumValue(
                    values,
                    prefix + "type",
                    PluginPropertyType.class,
                    PluginPropertyType.STRING);
            final var propertyValue = values.getOrDefault(prefix + "value", "").trim();
            validatePropertyValue(name, propertyType, propertyValue);
            final var definition
                    = new PluginPropertyDefinition(
                            name,
                            propertyValue,
                            propertyType,
                            getBoolean(values, prefix + "sensitive", false),
                            values.getOrDefault(prefix + "description", ""));

            result.put(name.toLowerCase(Locale.ROOT), definition);
        });

        return result;
    }

    /**
     * parse credentials some apis have credentials
     *
     * @param values key value pair
     * @return credentials mapped to plugin
     */
    private Map<String, CredentialReference> parseCredentials(
            final Map<String, String> values) {

        final var names = extractNames(values, "credential.");
        final Map<String, CredentialReference> result = new LinkedHashMap<>();

        names.forEach((var name) -> {
            final var prefix = "credential." + name + ".";
            final var reference = new CredentialReference(
                    name,
                    enumValue(values, prefix + "authentication-type", AuthenticationType.class),
                    require(values, prefix + "provider"),
                    require(values, prefix + "secret-reference"),
                    enumValue(values, prefix + "location", CredentialLocation.class, CredentialLocation.NONE),
                    values.getOrDefault(prefix + "parameter-name", ""));
            result.put(name.toLowerCase(Locale.ROOT), reference);
        });

        return result;
    }

    /**
     * extract value for keys
     *
     * @param values map of keys and values
     * @param rootPrefix start at this location
     * @return the key map
     */
    private static TreeSet<String> extractNames(
            final Map<String, String> values,
            final String rootPrefix) {

        final var names = new TreeSet<String>();
        for (var key : values.keySet()) {
            if (!key.startsWith(rootPrefix)) {
                continue;
            }

            final var remainder = key.substring(rootPrefix.length());
            final var separator = remainder.indexOf('.');
            if (separator > 0) {
                names.add(remainder.substring(0, separator));
            }
        }
        return names;
    }

    /**
     * get the properties
     *
     * @param values map of key values
     * @return the properties
     */
    private static TreeSet<String> extractPropertyNames(
            final Map<String, String> values) {

        final var rootPrefix = "property.";
        final var names = new TreeSet<String>();
        for (var key : values.keySet()) {
            if (!key.startsWith(rootPrefix)) {
                continue;
            }

            final var remainder = key.substring(rootPrefix.length());
            final var lastDot = remainder.lastIndexOf('.');
            if (lastDot > 0) {
                names.add(remainder.substring(0, lastDot));
            }
        }
        return names;
    }

    /**
     * build a smaller map from a larger map
     *
     * @param values original map
     * @param prefix start at this location
     * @return the new map
     */
    private static Map<String, String> subMap(
            final Map<String, String> values,
            final String prefix) {

        return values.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .collect(Collectors.toUnmodifiableMap(
                        entry -> entry.getKey().substring(prefix.length()),
                        Map.Entry::getValue));
    }

    /**
     * Check if a plugin parameter is present, its required
     *
     * @param values map to search
     * @param key key to find
     * @return the key
     */
    private static String require(
            final Map<String, String> values,
            final String key) {

        return Optional.ofNullable(values.get(normalise(key)))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new PluginDefinitionException(
                "Required plugin property is missing: " + key));
    }

    /**
     * look for optional values
     *
     * @param values map to check
     * @param key key to find
     * @return return the value if its there
     */
    private static Optional<String> optional(
            final Map<String, String> values,
            final String key) {

        return Optional.ofNullable(values.get(normalise(key)))
                .map(String::trim)
                .filter(value -> !value.isEmpty());
    }

    /**
     * check if a key is in the map, or if its a default value
     *
     * @param values map to check
     * @param key key to find
     * @param defaultValue default value to use
     * @return true if the key or default is present
     */
    private static boolean getBoolean(
            final Map<String, String> values,
            final String key,
            final boolean defaultValue) {

        final var value = values.get(normalise(key));
        if (value == null) {
            return defaultValue;
        }

        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" ->
                true;
            case "false", "no", "0", "off" ->
                false;
            default ->
                throw new PluginDefinitionException(
                        "Invalid boolean value for " + key + ": " + value);
        };
    }

    /**
     * get the integer value for a key
     *
     * @param values map to check
     * @param key key to check
     * @param defaultValue default value if key not founf
     * @return
     */
    private static int getInt(
            final Map<String, String> values,
            final String key,
            final int defaultValue) {

        final var value = values.get(normalise(key));
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new PluginDefinitionException(
                    "Invalid integer value for " + key + ": " + value,
                    exception);
        }
    }

    /**
     * get the enum from the map
     *
     * @param <E> the enum value to find
     * @param values map of values
     * @param key key to find
     * @param enumClass enum class
     * @return
     */
    private static <E extends Enum<E>> E enumValue(
            final Map<String, String> values,
            final String key,
            final Class<E> enumClass) {

        return enumValue(values, key, enumClass, null);
    }

    /**
     *
     * @param <E>
     * @param values
     * @param key
     * @param enumClass
     * @param defaultValue
     * @return
     */
    private static <E extends Enum<E>> E enumValue(
            final Map<String, String> values,
            final String key,
            final Class<E> enumClass,
            final E defaultValue) {

        final var value = values.get(normalise(key));
        if (value == null) {
            if (defaultValue != null) {
                return defaultValue;
            }
            throw new PluginDefinitionException(
                    "Required plugin property is missing: " + key);
        }

        try {
            return Enum.valueOf(
                    enumClass,
                    value.trim()
                            .replace('-', '_')
                            .toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new PluginDefinitionException(
                    "Invalid value for %s: %s".formatted(key, value),
                    exception);
        }
    }

    /**
     * Validates one declared property value against its configured type.
     *
     * @param propertyName property name
     * @param propertyType declared property type
     * @param propertyValue raw property value
     */
    private static void validatePropertyValue(
            final String propertyName,
            final PluginPropertyType propertyType,
            final String propertyValue) {
        if (propertyValue.isBlank()) {
            return;
        }
        try {
            switch (propertyType) {
                case STRING -> {
                    return;
                }
                case INTEGER -> Integer.parseInt(propertyValue);
                case LONG -> Long.parseLong(propertyValue);
                case BOOLEAN -> getBoolean(Map.of(propertyName, propertyValue), propertyName, false);
                case DECIMAL -> new BigDecimal(propertyValue);
                case DURATION -> Duration.parse(propertyValue);
                case PATH -> Path.of(propertyValue);
                case URI -> URI.create(propertyValue);
                default -> throw new PluginDefinitionException(
                        "Unsupported property type for " + propertyName + ": " + propertyType);
            }
        } catch (RuntimeException exception) {
            throw new PluginDefinitionException(
                    "Property '%s' does not match declared type %s."
                            .formatted(propertyName, propertyType),
                    exception);
        }
    }

    /**
     * normalize string
     * @param value
     * @return
     */
    private static String normalise(final String value) {
        return Objects.requireNonNull(value, "value")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
