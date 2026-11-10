/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.logging;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Shared per-thread logging context visible to both Core and provider plugins.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
public final class PluginLogContext {

    private static final ThreadLocal<Entry> CURRENT = new ThreadLocal<>();

    private PluginLogContext() {
        // Static utility.
    }

    /**
     * Opens a scope and restores the previous logging context when closed.
     *
     * @param pluginId current plugin identifier
     * @param runId current run identifier
     * @return scoped context handle
     */
    public static Scope open(String pluginId, UUID runId) {
        var previous = CURRENT.get();
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
     * @return the context for the current thread, if any
     */
    public static Optional<Entry> current() {
        return Optional.ofNullable(CURRENT.get());
    }

    /**
     * Immutable identifiers for one plugin run.
     *
     * @param pluginId plugin identifier
     * @param runId run identifier
     * @author Terry Curran
     * @since 4.1.0
     * @version 4.1.0
     */
    public record Entry(String pluginId, UUID runId) {
        /** Validates both record components. */
        public Entry {
            Objects.requireNonNull(pluginId, "pluginId");
            Objects.requireNonNull(runId, "runId");
        }
    }

    /**
     * Scope handle that restores the preceding thread context when closed.
     *
     * @author Terry Curran
     * @since 4.1.0
     * @version 4.1.0
     */
    @FunctionalInterface
    public interface Scope extends AutoCloseable {
        /** Restores the preceding logging context. */
        @Override
        void close();
    }
}
