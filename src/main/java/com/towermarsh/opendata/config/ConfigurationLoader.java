package com.towermarsh.opendata.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import com.towermarsh.opendata.cli.CommandLineArguments;
import com.towermarsh.opendata.exception.ConfigurationException;

/**
 * Loads and merges framework, application, plugin and user override properties.
 *
 * <p>Environment variables are deliberately not used. This preserves explicit,
 * repeatable execution through Apache Commons CLI and properties files.</p>
 */
public final class ConfigurationLoader {

    private static final String APPLICATION_RESOURCE = "config/application.properties";
    private static final String PLUGIN_RESOURCE_PATTERN = "config/plugins/%s.properties";

    private final ClassLoader classLoader;
    private final Map<String, String> builtInDefaults;

    public ConfigurationLoader() {
        this(Thread.currentThread().getContextClassLoader(), standardDefaults());
    }

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
     * @throws com.towermarsh.opendata.exception.ConfigurationException
     */
    public ApplicationConfig load(final CommandLineArguments arguments) throws ConfigurationException {
        Objects.requireNonNull(arguments, "arguments");

        final String pluginId = arguments.pluginId()
                .orElseThrow(() -> new ConfigurationException(
                        "A plugin is required before configuration can be loaded."));

        final Map<String, ResolvedConfigurationValue> merged = new LinkedHashMap<>();
        merge(merged, builtInDefaults, ConfigurationSource.BUILT_IN_DEFAULT);
        merge(merged, loadOptionalClasspathProperties(APPLICATION_RESOURCE),
                ConfigurationSource.APPLICATION_CLASSPATH);

        final String pluginResource = PLUGIN_RESOURCE_PATTERN.formatted(pluginId);
        final Map<String, String> pluginDefaults = loadRequiredClasspathProperties(pluginResource);
        merge(merged, pluginDefaults, ConfigurationSource.PLUGIN_CLASSPATH);

        arguments.overrideFile().ifPresent(path ->
                merge(merged, loadRequiredFileProperties(path), ConfigurationSource.OVERRIDE_FILE));

        final Map<String, String> values = new LinkedHashMap<>();
        merged.forEach((key, resolved) -> values.put(key, resolved.value()));

        return new ApplicationConfig(
                pluginId,
                values,
                arguments.overrideFile(),
                arguments.dryRun(),
                arguments.verbose());
    }

    private Map<String, String> loadOptionalClasspathProperties(final String resourceName) throws ConfigurationException {
        try (InputStream input = classLoader.getResourceAsStream(resourceName)) {
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

    private Map<String, String> loadRequiredClasspathProperties(final String resourceName) throws ConfigurationException {
        try (InputStream input = classLoader.getResourceAsStream(resourceName)) {
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

    private Map<String, String> loadRequiredFileProperties(final Path path) throws ConfigurationException {
        final Path normalised = path.toAbsolutePath().normalize();

        if (!Files.exists(normalised)) {
            throw new ConfigurationException("Configuration override file does not exist: " + normalised);
        }
        if (!Files.isRegularFile(normalised)) {
            throw new ConfigurationException("Configuration override path is not a file: " + normalised);
        }
        if (!Files.isReadable(normalised)) {
            throw new ConfigurationException("Configuration override file is not readable: " + normalised);
        }

        try (InputStream input = Files.newInputStream(normalised)) {
            return readProperties(input, normalised.toString());
        } catch (IOException exception) {
            throw new ConfigurationException(
                    "Unable to read configuration override file: " + normalised,
                    exception);
        }
    }

    private static Map<String, String> readProperties(
            final InputStream input,
            final String sourceDescription) throws ConfigurationException {

        final Properties properties = new Properties();
        try {
            properties.load(new java.io.InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new ConfigurationException(
                    "Unable to parse properties from " + sourceDescription,
                    exception);
        }

        final Map<String, String> result = new LinkedHashMap<>();
        for (String name : properties.stringPropertyNames()) {
            final String key = normaliseKey(name);
            final String value = Optional.ofNullable(properties.getProperty(name))
                    .orElse("")
                    .trim();

            if (result.put(key, value) != null) {
                throw new ConfigurationException(
                        "Duplicate configuration property after key normalisation: " + key);
            }
        }
        return result;
    }

    private static void merge(
            final Map<String, ResolvedConfigurationValue> target,
            final Map<String, String> source,
            final ConfigurationSource sourceType) {

        source.forEach((key, value) ->
                target.put(normaliseKey(key), new ResolvedConfigurationValue(value, sourceType)));
    }

    private static String normaliseKey(final String key) throws ConfigurationException {
        Objects.requireNonNull(key, "key");
        final String normalised = key.trim().toLowerCase(Locale.ROOT);
        if (normalised.isEmpty()) {
            throw new ConfigurationException("Configuration property names must not be blank.");
        }
        return normalised;
    }

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
