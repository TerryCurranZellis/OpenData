/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.plugin;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import java.lang.reflect.InvocationTargetException;

/**
 * Creates configured plugin classes named in plugin properties.
 *
 * @author Terry Curran
 * @since 3.3.0
 * @version 4.1.0
 */
public class ReflectionPluginFactory implements PluginFactory {

    /**
     * Creates one executable plugin instance from the resolved definition.
     *
     * @param plugin resolved plugin descriptor and definition
     * @return executable plugin instance
     */
    @Override
    public OpenDataPlugin create(ResolvedPlugin plugin) {
        var className = plugin.descriptor().implementationClass();
        try {
            var implementation = Class.forName(
                    className, true, Thread.currentThread().getContextClassLoader());
            var instance = instantiate(implementation, plugin.definition());
            if (!(instance instanceof OpenDataPlugin executable)) {
                throw new PluginRegistryException(
                        "Plugin class does not implement OpenDataPlugin: " + className);
            }
            return executable;
        } catch (ClassNotFoundException exception) {
            throw new PluginRegistryException("Plugin implementation class was not found: " + className, exception);
        } catch (ReflectiveOperationException exception) {
            var cause = exception instanceof InvocationTargetException invocation
                    && invocation.getCause() != null ? invocation.getCause() : exception;
            throw new PluginRegistryException("Unable to create plugin implementation: " + className, cause);
        }
    }

    /**
     * Creates the plugin using its configuration-aware constructor when available,
     * otherwise falling back to the public no-argument constructor.
     *
     * @param implementation plugin implementation class
     * @param definition resolved plugin configuration
     * @return newly created plugin instance
     * @throws ReflectiveOperationException if instantiate fails
     */
    private static Object instantiate(
            Class<?> implementation,
            PluginDefinition definition) throws ReflectiveOperationException {
        try {
            return implementation.getConstructor(PluginDefinition.class).newInstance(definition);
        } catch (NoSuchMethodException missingConfiguredConstructor) {
            return implementation.getConstructor().newInstance();
        }
    }
}
