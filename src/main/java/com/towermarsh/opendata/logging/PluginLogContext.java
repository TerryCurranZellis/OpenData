/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.logging;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Per-thread plugin and run identifiers added to every log line.
 *
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class PluginLogContext {

    private static final ThreadLocal<Entry> CURRENT = new ThreadLocal<>();

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
    public static Scope open(final String pluginId, final UUID runId) {
        final Entry previous = CURRENT.get();
        CURRENT.set(new Entry(pluginId, runId));
        return () -> {
            if (previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(previous);
            }
        };
    }

    /**
     *
     * Returns the current per-thread logging context, if any.
     *
     * @return current logging context
     *
     */
    public static Optional<Entry> current() {
        return Optional.ofNullable(CURRENT.get());
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
        /**
         * Validates and normalises record components.
         */
        public Entry {
            Objects.requireNonNull(pluginId, "pluginId");
            Objects.requireNonNull(runId, "runId");
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
