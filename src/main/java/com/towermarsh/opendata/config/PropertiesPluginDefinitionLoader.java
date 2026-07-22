package com.towermarsh.opendata.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

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
 * <p>The expected resource is
 * {@code config/plugins/<plugin-id>.properties}. Supplied overrides are merged
 * after the classpath properties and therefore take precedence.</p>
 */
public final class PropertiesPluginDefinitionLoader
        implements PluginDefinitionLoader {

    private static final String RESOURCE_PATTERN =
            "config/plugins/%s.properties";

    private final ClassLoader classLoader;

    public PropertiesPluginDefinitionLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public PropertiesPluginDefinitionLoader(final ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    @Override
    public PluginDefinition load(
            final String pluginId,
            final Map<String, String> overrides) {

        final String normalisedPluginId = normalise(pluginId);
        final String resourceName =
                RESOURCE_PATTERN.formatted(normalisedPluginId);

        final Map<String, String> values =
                loadRequiredResource(resourceName);
        Objects.requireNonNull(overrides, "overrides")
                .forEach((key, value) ->
                        values.put(normalise(key), value == null ? "" : value.trim()));

        final PluginDefinition definition = parse(values);
        if (!definition.id().equals(normalisedPluginId)) {
            throw new PluginDefinitionException(
                    "Selected plugin '%s' does not match plugin.id '%s'."
                            .formatted(normalisedPluginId, definition.id()));
        }

        new PluginDefinitionValidator().validate(definition);
        return definition;
    }

    PluginDefinition parse(final Map<String, String> values) {
        final String id = require(values, "plugin.id").toLowerCase(Locale.ROOT);
        final List<PluginEndpointDefinition> endpoints = parseEndpoints(values);
        final Map<String, PluginPropertyDefinition> properties =
                parsePluginProperties(values);
        final Map<String, CredentialReference> credentials =
                parseCredentials(values);

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

    private List<PluginEndpointDefinition> parseEndpoints(
            final Map<String, String> values) {

        final TreeSet<String> names = extractNames(values, "endpoint.");
        final List<PluginEndpointDefinition> result = new ArrayList<>();

        for (String name : names) {
            final String prefix = "endpoint." + name + ".";
            final Optional<LinkDiscoveryDefinition> discovery =
                    parseLinkDiscovery(values, prefix);

            result.add(new PluginEndpointDefinition(
                    name,
                    enumValue(values, prefix + "type", EndpointType.class),
                    URI.create(require(values, prefix + "url")),
                    enumValue(values, prefix + "method", HttpMethod.class),
                    DatasetFormat.parse(require(values, prefix + "format")),
                    enumValue(
                            values,
                            prefix + "strategy",
                            DownloadStrategyType.class),
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

    private Optional<LinkDiscoveryDefinition> parseLinkDiscovery(
            final Map<String, String> values,
            final String endpointPrefix) {

        final String prefix = endpointPrefix + "link-discovery.";
        if (!values.containsKey(prefix + "css-selector")) {
            return Optional.empty();
        }

        return Optional.of(new LinkDiscoveryDefinition(
                require(values, prefix + "css-selector"),
                require(values, prefix + "href-pattern"),
                values.getOrDefault(prefix + "text-pattern", ""),
                getBoolean(values, prefix + "select-last", false)));
    }

    private Map<String, PluginPropertyDefinition> parsePluginProperties(
            final Map<String, String> values) {

        final TreeSet<String> names = extractNames(values, "property.");
        final Map<String, PluginPropertyDefinition> result =
                new LinkedHashMap<>();

        for (String name : names) {
            final String prefix = "property." + name + ".";
            final PluginPropertyDefinition definition =
                    new PluginPropertyDefinition(
                            name,
                            require(values, prefix + "value"),
                            enumValue(
                                    values,
                                    prefix + "type",
                                    PluginPropertyType.class,
                                    PluginPropertyType.STRING),
                            getBoolean(values, prefix + "sensitive", false),
                            values.getOrDefault(prefix + "description", ""));

            result.put(name.toLowerCase(Locale.ROOT), definition);
        }

        return result;
    }

    private Map<String, CredentialReference> parseCredentials(
            final Map<String, String> values) {

        final TreeSet<String> names = extractNames(values, "credential.");
        final Map<String, CredentialReference> result =
                new LinkedHashMap<>();

        for (String name : names) {
            final String prefix = "credential." + name + ".";
            final CredentialReference reference = new CredentialReference(
                    name,
                    enumValue(
                            values,
                            prefix + "authentication-type",
                            AuthenticationType.class),
                    require(values, prefix + "provider"),
                    require(values, prefix + "secret-reference"),
                    enumValue(
                            values,
                            prefix + "location",
                            CredentialLocation.class,
                            CredentialLocation.NONE),
                    values.getOrDefault(prefix + "parameter-name", ""));

            result.put(name.toLowerCase(Locale.ROOT), reference);
        }

        return result;
    }

    private Map<String, String> loadRequiredResource(
            final String resourceName) {

        try (InputStream input =
                     classLoader.getResourceAsStream(resourceName)) {

            if (input == null) {
                throw new PluginDefinitionException(
                        "Plugin properties resource was not found: "
                                + resourceName);
            }

            final Properties properties = new Properties();
            properties.load(new InputStreamReader(
                    input,
                    StandardCharsets.UTF_8));

            return properties.stringPropertyNames().stream()
                    .collect(Collectors.toMap(
                            PropertiesPluginDefinitionLoader::normalise,
                            name -> properties.getProperty(name).trim(),
                            (first, second) -> second,
                            LinkedHashMap::new));
        } catch (IOException exception) {
            throw new PluginDefinitionException(
                    "Unable to read plugin properties resource: "
                            + resourceName,
                    exception);
        }
    }

    private static TreeSet<String> extractNames(
            final Map<String, String> values,
            final String rootPrefix) {

        final TreeSet<String> names = new TreeSet<>();
        for (String key : values.keySet()) {
            if (!key.startsWith(rootPrefix)) {
                continue;
            }

            final String remainder = key.substring(rootPrefix.length());
            final int separator = remainder.indexOf('.');
            if (separator > 0) {
                names.add(remainder.substring(0, separator));
            }
        }
        return names;
    }

    private static Map<String, String> subMap(
            final Map<String, String> values,
            final String prefix) {

        return values.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .collect(Collectors.toUnmodifiableMap(
                        entry -> entry.getKey().substring(prefix.length()),
                        Map.Entry::getValue));
    }

    private static String require(
            final Map<String, String> values,
            final String key) {

        return Optional.ofNullable(values.get(normalise(key)))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new PluginDefinitionException(
                        "Required plugin property is missing: " + key));
    }

    private static Optional<String> optional(
            final Map<String, String> values,
            final String key) {

        return Optional.ofNullable(values.get(normalise(key)))
                .map(String::trim)
                .filter(value -> !value.isEmpty());
    }

    private static boolean getBoolean(
            final Map<String, String> values,
            final String key,
            final boolean defaultValue) {

        final String value = values.get(normalise(key));
        if (value == null) {
            return defaultValue;
        }

        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> throw new PluginDefinitionException(
                    "Invalid boolean value for " + key + ": " + value);
        };
    }

    private static int getInt(
            final Map<String, String> values,
            final String key,
            final int defaultValue) {

        final String value = values.get(normalise(key));
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

    private static <E extends Enum<E>> E enumValue(
            final Map<String, String> values,
            final String key,
            final Class<E> enumClass) {

        return enumValue(values, key, enumClass, null);
    }

    private static <E extends Enum<E>> E enumValue(
            final Map<String, String> values,
            final String key,
            final Class<E> enumClass,
            final E defaultValue) {

        final String value = values.get(normalise(key));
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

    private static String normalise(final String value) {
        return Objects.requireNonNull(value, "value")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
