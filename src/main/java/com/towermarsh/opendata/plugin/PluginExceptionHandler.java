/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.exception.PluginException;
import java.util.Objects;

/**
 * Converts failures raised by plugin pipeline stages into the framework's
 * standard {@link PluginException}. Plugin implementations and their phase
 * packages must not define plugin-specific exception classes.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class PluginExceptionHandler {

    /** Executes a plugin and normalises any stage failure. */
    public PluginMetrics execute(
            final String pluginId,
            final OpenDataPlugin plugin,
            final PluginExecutionContext context) throws PluginException, InterruptedException {
        Objects.requireNonNull(pluginId, "pluginId");
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(context, "context");
        try {
            return plugin.execute(context);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw exception;
        } catch (PluginException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new PluginException(pluginId, failureMessage(exception), exception);
        }
    }

    private static String failureMessage(final Throwable failure) {
        final String message = failure.getMessage();
        return message == null || message.isBlank()
                ? "Plugin execution failed during " + failure.getClass().getSimpleName()
                : message;
    }
}
