/*
 * Filename: PluginLogContext.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.logging;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Per-thread plugin and run identifiers added to every log line. */
public final class PluginLogContext {
    private static final ThreadLocal<Entry> CURRENT = new ThreadLocal<>();

    private PluginLogContext() {
    }

    /**
     *
     * @param pluginId
     * @param runId
     * @return
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
     * @return
     */
    public static Optional<Entry> current() {
        return Optional.ofNullable(CURRENT.get());
    }

    public record Entry(String pluginId, UUID runId) {
        public Entry {
            Objects.requireNonNull(pluginId, "pluginId");
            Objects.requireNonNull(runId, "runId");
        }
    }

    /**
     *
     */
    @FunctionalInterface
    public interface Scope extends AutoCloseable {

        /**
         *
         */
        @Override
        void close();
    }
}
