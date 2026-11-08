/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.app.ApplicationInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Reads bootstrap settings from a writable deployment file, with a packaged
 * resource used only as a fallback before the first installation.
 *
 * <p>
 * The {@code init} switch is readable without a database or an AES key. A
 * successful wizard run is the only operation that changes it to false.</p>
 *
 * @author Terry Curran
 * @version 4.0.0
 */
public final class ApplicationBootstrapPropertiesLoader {

    /**
     * Default writable configuration file relative to the process directory.
     */
    public static final Path DEFAULT_FILE = Path.of("config", "application.properties");
    private final Path filePath;
    private final ClassLoader classLoader;
    private final ConfigurationPasswordCipher passwordCipher;

    /**
     * Creates a loader using the deployment configuration folder.
     *
     * @param passwordCipher password encryption implementation
     */
    public ApplicationBootstrapPropertiesLoader(ConfigurationPasswordCipher passwordCipher) {
        this(DEFAULT_FILE, Thread.currentThread().getContextClassLoader(), passwordCipher);
    }

    /**
     * Creates a loader with injectable file and resource dependencies.
     *
     * @param filePath writable property file
     * @param classLoader loader used for packaged initial defaults
     * @param passwordCipher password cipher
     */
    public ApplicationBootstrapPropertiesLoader(
            Path filePath, ClassLoader classLoader,
            ConfigurationPasswordCipher passwordCipher) {
        this.filePath = Objects.requireNonNull(filePath, "filePath");
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
        this.passwordCipher = Objects.requireNonNull(passwordCipher, "passwordCipher");
    }

    /**
     * Checks the first-run switch without reading secrets or opening SQL
     * Server.
     *
     * @return true exactly when {@code init=true} in the resolved properties
     */
    public boolean initialisationRequested() {
        return Boolean.parseBoolean(readProperties().getOrDefault("init", "false"));
    }

    /**
     * Returns the actual writable destination selected for this installation.
     *
     * @return property file path
     */
    public Path filePath() {
        return filePath;
    }

    /**
     * Reads the normal database credentials after initialisation is complete.
     *
     * @param overrides optional property overrides
     * @return validated bootstrap settings with decrypted password
     */
    public ApplicationBootstrapProperties load(Map<String, String> overrides) {
        var values = new LinkedHashMap<>(readProperties());
        values.putAll(Objects.requireNonNull(overrides, "overrides"));
        if (Boolean.parseBoolean(values.getOrDefault("init", "false"))) {
            throw new OpenDataConfigurationException(
                    "Initialisation is required. Start the OpenData GUI to run the setup wizard.");
        }
        var properties = new ApplicationPropertyValues(values);
        return new ApplicationBootstrapProperties(
                ApplicationInfo.current().version(),
                properties.requiredText("database.url"),
                properties.requiredText("database.user"),
                passwordCipher.decrypt(properties.requiredText("database.password")),
                properties.bool("application.use-database-properties", false));
    }

    /**
     * Persists the bootstrap settings, preserving other file properties.
     * Existing callers use this for normal registration; setup calls the
     * overload accepting an explicit key location.
     *
     * @param bootstrapProperties plain-text bootstrap credentials
     */
    public void store(ApplicationBootstrapProperties bootstrapProperties) {
        storeInternal(bootstrapProperties, null);
    }

    /**
     * Completes first-run installation by persisting database and AES settings
     * and setting {@code init=false} in an atomic file replacement.
     *
     * @param bootstrapProperties newly provisioned application connection
     * @param keyFile absolute external AES key path
     */
    public void completeInitialisation(
            ApplicationBootstrapProperties bootstrapProperties, Path keyFile) {
        storeInternal(bootstrapProperties,
                Objects.requireNonNull(keyFile, "keyFile").toAbsolutePath().normalize());
    }

    /**
     * Writes a complete bootstrap file through a temporary file to avoid
     * truncating the previous settings if a write fails.
     *
     * @param bootstrap plain-text credentials
     * @param keyFile key path for setup, or null to preserve existing setting
     */
    private void storeInternal(ApplicationBootstrapProperties bootstrap, Path keyFile) {
        Objects.requireNonNull(bootstrap, "bootstrap");
        var values = new LinkedHashMap<>(readProperties());
        values.putAll(bootstrap.toFileValues(passwordCipher.encrypt(bootstrap.databasePassword())));
        if (keyFile != null) {
            values.put(AesConfigurationPasswordCipher.KEY_FILE_SETTING, keyFile.toString());
        }
        values.put("init", "false");
        var output = new Properties();
        values.forEach(output::setProperty);
        Path temporary = null;
        try {
            var target = filePath.toAbsolutePath().normalize();
            var directory = Objects.requireNonNull(target.getParent(), "bootstrap directory");
            Files.createDirectories(directory);
            temporary = Files.createTempFile(directory, "application-", ".properties.tmp");
            try (var writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                output.store(writer, "OpenData bootstrap configuration v4.0.0");
            }
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new OpenDataConfigurationException("Unable to write application bootstrap properties.", exception);
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException ignored) {
                    // An unused temporary file can be cleaned up manually.
                }
            }
        }
    }

    /**
     * Opens packaged bootstrap defaults using either the traditional classpath
     * or the application's named Java module.
     *
     * @return resource input stream, or null if not found
     * @throws IOException if the module resource cannot be opened
     */
    private InputStream openPackagedResource() throws IOException {

        var resource = ClasspathConfigurationPropertiesSource.APPLICATION_RESOURCE;

        // Traditional classpath loading.
        var input = classLoader.getResourceAsStream(resource);

        // Named-module loading used by javafx:run.
        if (input == null) {
            input = ApplicationBootstrapPropertiesLoader.class
                    .getModule()
                    .getResourceAsStream(resource);
        }

        return input;
    }

    /**
     * Loads file settings, falling back to packaged defaults for first run.
     *
     * @return normalised, mutable map of bootstrap settings
     */
    private Map<String, String> readProperties() {
        if (Files.isRegularFile(filePath)) {
            try (var input
                    = Files.newInputStream(filePath); var reader
                    = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                return load(reader);
            } catch (IOException exception) {
                throw new OpenDataConfigurationException("Unable to read bootstrap file: " + filePath, exception);
            }
        }
        try (var input = openPackagedResource()) {
            if (input == null) {
                throw new OpenDataConfigurationException("Application bootstrap resource was not found.");
            }
            return load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            throw new OpenDataConfigurationException("Unable to read bootstrap defaults.", exception);
        }
    }

    /**
     * Parses a standard UTF-8 Java properties stream.
     *
     * @param reader input reader
     * @return lower-case-key map
     * @throws IOException if the stream cannot be read
     */
    private static Map<String, String> load(InputStreamReader reader) throws IOException {
        var properties = new Properties();
        properties.load(reader);
        Map<String, String> values = new LinkedHashMap<>();
        properties.stringPropertyNames().forEach(name -> values.put(
                name.trim().toLowerCase(java.util.Locale.ROOT), properties.getProperty(name).trim()));
        return values;
    }
}
