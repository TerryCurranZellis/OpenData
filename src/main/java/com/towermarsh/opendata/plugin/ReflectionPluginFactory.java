/*
 * Filename: ReflectionPluginFactory.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.lang.reflect.InvocationTargetException;

/** Creates configured plugin classes named in plugin properties.  *
* @author Terry Curran
* @version 17 July 2026
*/
public final class ReflectionPluginFactory implements PluginFactory {
    @Override
    public OpenDataPlugin create(final ResolvedPlugin plugin) {
        final String className = plugin.descriptor().implementationClass();
        try {
            final Class<?> implementation = Class.forName(
                    className, true, Thread.currentThread().getContextClassLoader());
            final Object instance = instantiate(implementation, plugin.definition());
            if (!(instance instanceof OpenDataPlugin executable)) {
                throw new PluginRegistryException(
                        "Plugin class does not implement OpenDataPlugin: " + className);
            }
            return executable;
        } catch (ClassNotFoundException exception) {
            throw new PluginRegistryException("Plugin implementation class was not found: " + className, exception);
        } catch (ReflectiveOperationException exception) {
            final Throwable cause = exception instanceof InvocationTargetException invocation
                    && invocation.getCause() != null ? invocation.getCause() : exception;
            throw new PluginRegistryException("Unable to create plugin implementation: " + className, cause);
        }
    }

    private static Object instantiate(
            final Class<?> implementation,
            final PluginDefinition definition) throws ReflectiveOperationException {
        try {
            return implementation.getConstructor(PluginDefinition.class).newInstance(definition);
        } catch (NoSuchMethodException missingConfiguredConstructor) {
            return implementation.getConstructor().newInstance();
        }
    }
}
