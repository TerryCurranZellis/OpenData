/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.common.logging;

import java.util.Optional;
import java.util.UUID;

/**
 * Per-thread plugin and run identifiers added to every log line.
 *
 *
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 * @deprecated Use {@link com.towermarsh.opendata.plugin.PluginLogContext} instead.
 */
@Deprecated(since = "4.1.0")
public final class PluginLogContext {

    /**
     * Prevents instantiation of this utility class.
     */
    private PluginLogContext() {
    }

    /**
     *
     * Opens a per-thread logging scope for one plugin run.
     *
     * @param pluginId plugin identifier
     * @param runId plugin run identifier
     * @return scope that restores the previous thread context when closed
     */
    public static Scope open(String pluginId, UUID runId) {
        var scope = com.towermarsh.opendata.plugin.PluginLogContext.open(pluginId, runId);
        return scope::close;
    }

    /**
     *
     * Returns the current per-thread logging context, if any.
     *
     * @return current logging context
     *
     */
    public static Optional<Entry> current() {
        return com.towermarsh.opendata.plugin.PluginLogContext.current()
                .map(context -> new Entry(context.pluginId(), context.runId()));
    }

    /**
     * Immutable plugin logging context stored for one thread.
     *
     * @param pluginId plugin identifier associated with the current thread
     * @param runId plugin run identifier associated with the current thread
     */
    public record Entry(String pluginId, UUID runId) {

        /**
         * Validates and normalises record components.
         */
        public Entry {
            java.util.Objects.requireNonNull(pluginId, "pluginId");
            java.util.Objects.requireNonNull(runId, "runId");
        }
    }

    /**
     *
     * Auto-closeable handle for restoring the previous thread logging context.
     */
    @FunctionalInterface
    public interface Scope extends AutoCloseable {

        /**
         * Restores the previous thread logging context.
         */
        @Override
        void close();
    }
}
