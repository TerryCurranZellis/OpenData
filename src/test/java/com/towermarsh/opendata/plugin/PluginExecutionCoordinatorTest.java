/*
 * Filename: PluginExecutionCoordinatorTest.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class PluginExecutionCoordinatorTest {
    @Test
    void runsTwoPluginsConcurrentlyAndPreservesResultOrder() throws Exception {
        final var active = new AtomicInteger();
        final var maximum = new AtomicInteger();
        final PluginFactory factory = ignored -> context -> {
            final int now = active.incrementAndGet();
            maximum.accumulateAndGet(now, Math::max);
            try {
                Thread.sleep(150);
                return new PluginMetrics(1, 1, 0, 0);
            } finally {
                active.decrementAndGet();
            }
        };
        final DatabaseResourceManager database = new DatabaseResourceManager() {
            @Override
            public Connection getConnection() throws SQLException {
                throw new SQLException("not used");
            }

            @Override
            public void close() {
            }
        };
        final var coordinator = new PluginExecutionCoordinator(
                factory,
                new NoOpPluginRunAudit(),
                database,
                Clock.systemUTC(),
                Duration.ofSeconds(2));

        final var summary = coordinator.execute(
                List.of(plugin("first"), plugin("second")), 2, true);

        assertEquals(2, maximum.get());
        assertEquals(List.of("first", "second"),
                summary.results().stream().map(PluginRunResult::pluginId).toList());
        assertTrue(summary.allSuccessful());
    }


    @Test
    void isolatesFailureAndAllowsOtherPluginToComplete() throws Exception {
        final PluginFactory factory = resolved -> context -> {
            if ("broken".equals(resolved.descriptor().id())) {
                throw new IllegalStateException("expected failure");
            }
            return new PluginMetrics(1, 1, 0, 0);
        };
        final DatabaseResourceManager database = new DatabaseResourceManager() {
            @Override
            public Connection getConnection() throws SQLException {
                throw new SQLException("not used");
            }

            @Override
            public void close() {
            }
        };
        final var coordinator = new PluginExecutionCoordinator(
                factory,
                new NoOpPluginRunAudit(),
                database,
                Clock.systemUTC(),
                Duration.ofSeconds(2));

        final var summary = coordinator.execute(
                List.of(plugin("broken"), plugin("working")), 2, true);

        assertEquals(PluginRunStatus.FAILED, summary.results().get(0).status());
        assertEquals(PluginRunStatus.DRY_RUN, summary.results().get(1).status());
        assertEquals(1L, summary.succeeded());
        assertEquals(1L, summary.failed());
    }

    private static ResolvedPlugin plugin(final String id) {
        final var descriptor = new PluginDescriptor(id, id, "", "example." + id, true, 1);
        final var definition = new PluginDefinition(
                id, id, "", "example." + id, true, 1, id, List.of(), Map.of(), Map.of());
        return new ResolvedPlugin(descriptor, definition);
    }
}
