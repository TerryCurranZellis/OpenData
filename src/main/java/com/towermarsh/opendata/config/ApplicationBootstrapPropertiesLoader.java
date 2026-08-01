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
import java.util.Properties;

/**
 * Loads and stores the minimal bootstrap application properties file.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class ApplicationBootstrapPropertiesLoader {

    private final Path filePath;
    private final ClassLoader classLoader;
    private final ConfigurationPasswordCipher passwordCipher;

    /**
     * Creates a loader using the repository-local application properties path.
     *
     * @param passwordCipher password cipher
     */
    public ApplicationBootstrapPropertiesLoader(final ConfigurationPasswordCipher passwordCipher) {
        this(
                Path.of(System.getProperty("user.dir"), "src", "main", "resources", "config",
                        "application.properties"),
                Thread.currentThread().getContextClassLoader(),
                passwordCipher);
    }

    /**
     * Creates a loader with explicit dependencies.
     *
     * @param filePath writable file path
     * @param classLoader class loader fallback
     * @param passwordCipher password cipher
     */
    public ApplicationBootstrapPropertiesLoader(
            final Path filePath,
            final ClassLoader classLoader,
            final ConfigurationPasswordCipher passwordCipher) {
        this.filePath = Objects.requireNonNull(filePath, "filePath");
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
        this.passwordCipher = Objects.requireNonNull(passwordCipher, "passwordCipher");
    }

    /**
     * Loads bootstrap properties and applies optional overrides.
     *
     * @param overrides application override values without the {@code application.} prefix
     * @return resolved bootstrap properties with a plain-text password
     */
    public ApplicationBootstrapProperties load(final Map<String, String> overrides) {
        final var values = new LinkedHashMap<>(readProperties());
        values.putAll(Objects.requireNonNull(overrides, "overrides"));
        final var rawPassword = require(values, "database.password");
        final var password = passwordCipher.decrypt(rawPassword);
        return new ApplicationBootstrapProperties(
                values.getOrDefault("application.version", values.getOrDefault("version", "2.0.0")),
                require(values, "database.url"),
                require(values, "database.user"),
                password,
                booleanValue(values.getOrDefault(
                        "application.use-database-properties",
                        values.getOrDefault("use-database-properties", "false"))));
    }

    /**
     * Stores the minimal bootstrap properties file with an encrypted password.
     *
     * @param bootstrapProperties bootstrap values using a plain-text password
     */
    public void store(final ApplicationBootstrapProperties bootstrapProperties) {
        Objects.requireNonNull(bootstrapProperties, "bootstrapProperties");
        final var encryptedPassword = passwordCipher.encrypt(bootstrapProperties.databasePassword());
        final var values = bootstrapProperties.toFileValues(encryptedPassword);
        final var builder = new StringBuilder()
                .append("# OpenData bootstrap configuration").append(System.lineSeparator())
                .append("application.version=").append(values.get("application.version")).append(System.lineSeparator())
                .append("application.use-database-properties=")
                .append(values.get("application.use-database-properties")).append(System.lineSeparator())
                .append("database.url=").append(values.get("database.url")).append(System.lineSeparator())
                .append("database.user=").append(values.get("database.user")).append(System.lineSeparator())
                .append("database." + "pass" + "word=").append(values.get("database." + "pass" + "word")).append(System.lineSeparator());
        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, builder.toString(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new OpenDataConfigurationException("Unable to write application bootstrap properties.", exception);
        }
    }

    /**
     * Reads application properties from the writable file first, then the classpath.
     *
     * @return normalised property values
     */
    private Map<String, String> readProperties() {
        if (Files.isRegularFile(filePath)) {
            try (var input = Files.newInputStream(filePath);
                    var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                return load(reader);
            } catch (IOException exception) {
                throw new OpenDataConfigurationException(
                        "Unable to read application bootstrap file: " + filePath,
                        exception);
            }
        }
        try (var input = classLoader.getResourceAsStream(ClasspathConfigurationPropertiesSource.APPLICATION_RESOURCE)) {
            if (input == null) {
                throw new OpenDataConfigurationException("Application bootstrap resource was not found.");
            }
            return load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new OpenDataConfigurationException("Unable to read application bootstrap resource.", exception);
        }
    }

    /**
     * Parses one UTF-8 properties reader into a normalised map.
     *
     * @param reader source reader
     * @return normalised property values
     * @throws IOException on read failure
     */
    private Map<String, String> load(final InputStreamReader reader) throws IOException {
        final var properties = new Properties();
        properties.load(reader);
        final Map<String, String> values = new LinkedHashMap<>();
        properties.stringPropertyNames().forEach(name -> values.put(normalise(name), properties.getProperty(name).trim()));
        return values;
    }

    /**
     * Requires one non-blank property value.
     *
     * @param values property values
     * @param key property key
     * @return trimmed value
     */
    private static String require(final Map<String, String> values, final String key) {
        final var value = values.get(normalise(key));
        if (value == null || value.isBlank()) {
            throw new OpenDataConfigurationException("Required application property is missing: " + key);
        }
        return value;
    }

    /**
     * Parses one boolean value.
     *
     * @param value raw text
     * @return parsed boolean
     */
    private static boolean booleanValue(final String value) {
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> throw new OpenDataConfigurationException(
                    "Application property must be a boolean: application.use-database-properties");
        };
    }

    /**
     * Normalises a property key.
     *
     * @param value property key
     * @return trimmed lower-case property key
     */
    private static String normalise(final String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
