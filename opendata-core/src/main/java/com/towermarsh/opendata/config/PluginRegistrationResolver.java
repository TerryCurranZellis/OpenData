/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.core.plugin.ClasspathPluginRegistry;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginDescriptor;
import com.towermarsh.opendata.core.plugin.PluginRegistryException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Resolves validated plugin registrations from packaged or external properties.
 *
 * <p>This shared resolver contains the definition-loading behaviour used by both
 * the command-line administration path and the JavaFX administration path. It
 * deliberately performs no database writes.</p>
 *
 * @author Terry Curran
 * @since 3.0.0
 * @version 4.1.0
 */
public final class PluginRegistrationResolver {

    private final ClassLoader classLoader;

    /**
     * Creates a resolver using the thread context class loader.
     */
    public PluginRegistrationResolver() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Creates a resolver using an explicit class loader.
     *
     * @param classLoader class loader containing packaged plugin resources
     */
    public PluginRegistrationResolver(final ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    /**
     * Lists the packaged plugin catalogue in identifier order.
     *
     * @return immutable packaged plugin descriptors
     */
    public List<PluginDescriptor> packagedPlugins() {
        return new ClasspathPluginRegistry(classLoader).list();
    }

    /**
     * Resolves all packaged plugin definitions.
     *
     * @return validated packaged registrations
     */
    public List<PluginRegistration> resolveAllPackaged() {
        return resolvePackaged(packagedPlugins().stream().map(PluginDescriptor::id).toList());
    }

    /**
     * Resolves named packaged plugin definitions.
     *
     * @param pluginIds packaged plugin identifiers
     * @return validated packaged registrations in request order
     */
    public List<PluginRegistration> resolvePackaged(final Collection<String> pluginIds) {
        Objects.requireNonNull(pluginIds, "pluginIds");
        var catalog = new ClasspathPluginRegistry(classLoader);
        var source = new ClasspathConfigurationPropertiesSource(classLoader);
        var loader = new PropertiesPluginDefinitionLoader(source);
        var result = new ArrayList<PluginRegistration>();

        for (var pluginId : pluginIds) {
            var descriptor = catalog.find(pluginId)
                    .orElseThrow(() -> new PluginRegistryException(
                    "Packaged plugin definition was not found: " + pluginId));
            var properties = source.loadPluginProperties(descriptor.id());
            var definition = loader.load(descriptor.id(), Map.of());
            validateImplementation(definition.implementationClass());
            result.add(new PluginRegistration(toDescriptor(definition), properties));
        }
        return List.copyOf(result);
    }

    /**
     * Resolves an external plugin definition and verifies the requested id.
     *
     * @param requestedPluginId expected plugin identifier
     * @param file complete plugin properties file
     * @return validated plugin registration
     */
    public PluginRegistration resolveFile(
            String requestedPluginId,
            Path file) {
        Objects.requireNonNull(requestedPluginId, "requestedPluginId");
        var source = new PropertiesFileConfigurationPropertiesSource(file);
        var properties = source.loadPluginProperties(requestedPluginId);
        var definition = new PropertiesPluginDefinitionLoader(source)
                .load(requestedPluginId, Map.of());
        validateImplementation(definition.implementationClass());
        return new PluginRegistration(toDescriptor(definition), properties);
    }

    /**
     * Resolves an external plugin definition using its own {@code plugin.id}
     * property as the requested identifier.
     *
     * @param file complete plugin properties file
     * @return validated plugin registration
     */
    public PluginRegistration resolveFile(final Path file) {
        var source = new PropertiesFileConfigurationPropertiesSource(file);
        var properties = source.loadPluginProperties("external");
        var pluginId = Objects.toString(properties.get("plugin.id"), "").trim();
        if (pluginId.isEmpty()) {
            throw new PluginDefinitionException(
                    "Required plugin property is missing: plugin.id");
        }
        var definition = new PropertiesPluginDefinitionLoader(source)
                .load(pluginId, Map.of());
        validateImplementation(definition.implementationClass());
        return new PluginRegistration(toDescriptor(definition), properties);
    }

    /**
     * Converts a value to descriptor.
     *
     * @param definition input definition
     * @return result of to descriptor
     */
    private static PluginDescriptor toDescriptor(final PluginDefinition definition) {
        return new PluginDescriptor(
                definition.id(),
                definition.displayName(),
                definition.description(),
                definition.implementationClass(),
                definition.enabled(),
                definition.configurationVersion());
    }

    /**
     * Validates implementation.
     *
     * @param className requested class name
     */
    private void validateImplementation(final String className) {
        try {
            var implementation = Class.forName(
                    className,
                    false,
                    classLoader);
            if (!OpenDataPlugin.class.isAssignableFrom(implementation)) {
                throw new PluginRegistryException(
                        "Plugin class does not implement OpenDataPlugin: " + className);
            }
        } catch (ClassNotFoundException exception) {
            throw new PluginRegistryException(
                    ("Plugin implementation class was not found on the application classpath: %s. "
                            + "Check the plugin dependency and plugin.implementation-class.")
                            .formatted(className),
                    exception);
        }
    }
}
