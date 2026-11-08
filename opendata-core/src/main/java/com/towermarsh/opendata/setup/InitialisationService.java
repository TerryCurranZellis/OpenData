/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.setup;

import com.towermarsh.opendata.app.ApplicationInfo;
import com.towermarsh.opendata.config.AesConfigurationPasswordCipher;
import com.towermarsh.opendata.config.ApplicationBootstrapProperties;
import com.towermarsh.opendata.config.ApplicationBootstrapPropertiesLoader;
import com.towermarsh.opendata.config.ApplicationRuntimeConfiguration;
import com.towermarsh.opendata.config.ClasspathConfigurationPropertiesSource;
import com.towermarsh.opendata.config.ConfigurationPasswordCipher;
import com.towermarsh.opendata.config.JdbcConfigurationPropertiesSource;
import com.towermarsh.opendata.config.OpenDataConfigurationException;
import com.towermarsh.opendata.config.PluginConfigurationDirectoryScanner;
import com.towermarsh.opendata.config.PluginRegistration;
import com.towermarsh.opendata.config.PluginRegistrationResolver;
import com.towermarsh.opendata.core.database.SQLServerResource;
import com.towermarsh.opendata.core.plugin.JdbcPluginRegistry;
import com.towermarsh.opendata.core.plugin.PluginRegistryException;
import com.towermarsh.opendata.plugin.PluginDescriptor;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Coordinates first-run configuration, SQL database provisioning and disabled
 * plugin registration. It is independent of JavaFX to permit headless tests.
 * The init flag is only cleared after every database operation succeeds.
 *
 * @author Terry Curran
 * @version 4.0.0
 */
public final class InitialisationService {

    private final ApplicationBootstrapPropertiesLoader loader;
    private final PluginRegistrationResolver resolver;
    private final PluginConfigurationDirectoryScanner scanner;
    private final SqlServerInitialiser sqlInitialiser;

    /** Creates the production wizard backend. */
    public InitialisationService() {
        this(new ApplicationBootstrapPropertiesLoader(new AesConfigurationPasswordCipher()),
                new PluginRegistrationResolver(), new PluginConfigurationDirectoryScanner(),
                new SqlServerInitialiser());
    }

    /**
     * Creates an independently testable service.
     *
     * @param loader property file loader
     * @param resolver plugin definition resolver
     * @param scanner deployment plugin config scanner
     * @param sqlInitialiser database schema installer
     */
    public InitialisationService(
            final ApplicationBootstrapPropertiesLoader loader,
            final PluginRegistrationResolver resolver,
            final PluginConfigurationDirectoryScanner scanner,
            final SqlServerInitialiser sqlInitialiser) {
        this.loader = Objects.requireNonNull(loader, "loader");
        this.resolver = Objects.requireNonNull(resolver, "resolver");
        this.scanner = Objects.requireNonNull(scanner, "scanner");
        this.sqlInitialiser = Objects.requireNonNull(sqlInitialiser, "sqlInitialiser");
    }

    /**
     * Installs and registers all validated available plugins, always disabled.
     * The only persisted secrets are AES-encrypted application credentials;
     * server administrator credentials are never written to disk or SQL.
     *
     * @param request validated wizard input
     * @param progress callback used to populate the wizard progress log
     * @return identifiers of registered disabled plugins
     */
    public List<String> initialise(
            final InitialisationRequest request, final Consumer<String> progress) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(progress, "progress");
        if (!loader.initialisationRequested()) {
            throw new OpenDataConfigurationException(
                    "Initialisation has already completed; set init=true intentionally before rerunning setup.");
        }
        progress.accept("Discovering and validating plugin configuration files...");
        final var registrations = resolvePlugins();
        progress.accept(registrations.size() + " plugin definition(s) validated.");
        prepareKey(request.aesKeyFile());
        final ConfigurationPasswordCipher cipher = new AesConfigurationPasswordCipher(request.aesKeyFile());
        final var bootstrap = new ApplicationBootstrapProperties(
                ApplicationInfo.current().version(), request.jdbcUrl(request.database()),
                request.applicationUser(), request.applicationPassword(), true);
        sqlInitialiser.initialise(request, progress);
        sqlInitialiser.installPluginSchemas(request, registrations, progress);
        progress.accept("Connecting using the newly configured application login...");
        try (var database = SQLServerResource.initialise(bootstrap.toDatabasePoolConfiguration())) {
            final var registry = new JdbcPluginRegistry(database);
            progress.accept("Recording OpenData application settings...");
            final var values = new LinkedHashMap<>(ApplicationRuntimeConfiguration.defaultPropertyValues());
            values.putAll(new ClasspathConfigurationPropertiesSource().loadApplicationProperties());
            values.put("application.version", ApplicationInfo.current().version());
            values.put("application.use-database-properties", "true");
            values.put("database.url", bootstrap.databaseUrl());
            values.put("database.user", bootstrap.databaseUser());
            values.put("database.password", cipher.encrypt(bootstrap.databasePassword()));
            values.put("security.aes-key-file", request.aesKeyFile().toString());
            values.put("init", "false");
            new JdbcConfigurationPropertiesSource(database)
                    .saveApplicationProperties(values, List.of("database.password"));
            for (var registration : registrations) {
                final var disabled = asDisabled(registration);
                progress.accept("Registering " + disabled.descriptor().id() + " (disabled)...");
                registry.register(disabled.descriptor(), disabled.properties());
                registry.setEnabled(disabled.descriptor().id(), false);
            }
        }
        progress.accept("Saving encrypted bootstrap settings and clearing init flag...");
        // The loader provided by the caller need not use the same cipher as the
        // request. Persist with the same AES key that was just created.
        new ApplicationBootstrapPropertiesLoader(loader.filePath(),
                Thread.currentThread().getContextClassLoader(), cipher)
                .completeInitialisation(bootstrap, request.aesKeyFile());
        progress.accept("Initialisation completed. All plugins are disabled.");
        return registrations.stream().map(r -> r.descriptor().id()).toList();
    }

    /**
     * Finds both packaged JAR plugins and explicitly deployed property files.
     * File definitions take precedence for duplicate plugin IDs.
     *
     * @return validated registrations in stable ID order
     */
    public List<PluginRegistration> resolvePlugins() {
        final Map<String, PluginRegistration> registrations = new LinkedHashMap<>();
        try {
            for (var packaged : resolver.resolveAllPackaged()) {
                registrations.put(packaged.descriptor().id(), packaged);
            }
        } catch (PluginRegistryException exception) {
            // An external-only installation has no packaged registry index.
            // Do not suppress damaged or inconsistent packaged indexes.
            if (exception.getMessage() == null || !exception.getMessage().startsWith(
                    "Plugin registry resource was not found: config/plugins/index.properties")) {
                throw exception;
            }
        }
        final Map<String, Path> paths = new LinkedHashMap<>();
        for (var file : scanner.scan()) {
            final var registration = resolver.resolveFile(file);
            final var previous = paths.putIfAbsent(registration.descriptor().id(), file);
            if (previous != null && !previous.equals(file)) {
                throw new OpenDataConfigurationException("Duplicate plugin.id in " + previous + " and " + file);
            }
            registrations.put(registration.descriptor().id(), registration);
        }
        return registrations.values().stream()
                .sorted(java.util.Comparator.comparing(r -> r.descriptor().id())).toList();
    }

    /**
     * Creates a disabled descriptor and property set without mutating source
     * property files or the validated original registration.
     *
     * @param source validated registration
     * @return disabled registration
     */
    public static PluginRegistration asDisabled(final PluginRegistration source) {
        Objects.requireNonNull(source, "source");
        final var descriptor = source.descriptor();
        final var disabled = new PluginDescriptor(descriptor.id(), descriptor.displayName(),
                descriptor.description(), descriptor.implementationClass(), false,
                descriptor.configurationVersion());
        final var properties = new LinkedHashMap<>(source.properties());
        properties.put("plugin.enabled", "false");
        return new PluginRegistration(disabled, properties);
    }

    /**
     * Creates a strong AES key only if the selected file is absent. Existing
     * key files must contain exactly 32 random bytes in Base64; they are never
     * overwritten. POSIX platforms receive owner-only file permissions.
     *
     * @param keyFile destination outside source control
     */
    public static void prepareKey(final Path keyFile) {
        Objects.requireNonNull(keyFile, "keyFile");
        try {
            if (Files.exists(keyFile)) {
                final var decoded = Base64.getDecoder().decode(
                        Files.readString(keyFile, StandardCharsets.US_ASCII).trim());
                if (decoded.length != 32) {
                    throw new OpenDataConfigurationException("Existing AES key must be 32 Base64-encoded bytes.");
                }
                java.util.Arrays.fill(decoded, (byte) 0);
                return;
            }
            final var parent = keyFile.toAbsolutePath().getParent();
            Files.createDirectories(parent);
            final var bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            try {
                final var content = Base64.getEncoder().encodeToString(bytes) + System.lineSeparator();
                try {
                    Files.writeString(keyFile, content, StandardCharsets.US_ASCII,
                            java.nio.file.StandardOpenOption.CREATE_NEW);
                } catch (java.nio.file.FileAlreadyExistsException exception) {
                    throw new OpenDataConfigurationException("AES key file appeared during setup; retry without overwriting it.", exception);
                }
                try {
                    Files.setPosixFilePermissions(keyFile,
                            EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
                } catch (UnsupportedOperationException ignored) {
                    // On Windows, the administrator must restrict the NTFS ACL.
                }
            } finally {
                java.util.Arrays.fill(bytes, (byte) 0);
            }
        } catch (IOException | IllegalArgumentException exception) {
            throw new OpenDataConfigurationException("Unable to prepare AES key file: " + keyFile, exception);
        }
    }
}
