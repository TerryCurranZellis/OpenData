/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.plugin;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.plugin.PluginDescriptor;
import com.towermarsh.opendata.plugin.PluginRegistrar;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Finds and runs the {@link PluginRegistrar} that each plugin supplies as a
 * {@code RegisterPlugin} class beside its {@code OpenDataPlugin} implementation.
 *
 * @author Terry Curran
 * @since 3.1.0
 * @version 4.1.0
 */
public final class PluginRegistrarLocator {

    private static final Logger LOGGER = Logger.getLogger(PluginRegistrarLocator.class.getName());

    private ClassLoader classLoader;

    /** Creates a locator using the thread context class loader. */
    public PluginRegistrarLocator() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Creates a locator using an explicit class loader.
     *
     * @param classLoader class loader holding plugin classes
     */
    public PluginRegistrarLocator(ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    /**
     * Locates the registrar for a plugin.
     *
     * @param descriptor plugin descriptor
     * @return registrar, or empty when the plugin ships none
     */
    public Optional<PluginRegistrar> find(PluginDescriptor descriptor) {
        var implementation = Objects.requireNonNull(descriptor, "descriptor").implementationClass();
        var dot = implementation.lastIndexOf('.');
        var registrarName = (dot < 0 ? "" : implementation.substring(0, dot + 1))
                + PluginRegistrar.CONVENTIONAL_CLASS_NAME;
        Class<?> type;
        try {
            type = Class.forName(registrarName, true, classLoader);
        } catch (ClassNotFoundException exception) {
            return Optional.empty();
        } catch (LinkageError exception) {
            throw new PluginRegistryException(
                    "Plugin registration class could not be loaded: " + registrarName, exception);
        }
        if (!PluginRegistrar.class.isAssignableFrom(type)) {
            throw new PluginRegistryException(
                    "Plugin registration class does not implement PluginRegistrar: " + registrarName);
        }
        try {
            return Optional.of((PluginRegistrar) type.getDeclaredConstructor().newInstance());
        } catch (ReflectiveOperationException exception) {
            throw new PluginRegistryException(
                    "Plugin registration class could not be created: " + registrarName, exception);
        }
    }

    /**
     * Runs the plugin's registration hook, if any, so its database objects exist.
     *
     * @param descriptor plugin descriptor
     * @param database database resource manager
     */
    public void install(PluginDescriptor descriptor, DatabaseResourceManager database) {
        find(descriptor).ifPresentOrElse(
                registrar -> {
                    LOGGER.info(() -> "Running registration hook for plugin " + descriptor.id());
                    registrar.register(database);
                },
                () -> LOGGER.info(() -> "Plugin " + descriptor.id() + " ships no RegisterPlugin hook."));
    }
}
