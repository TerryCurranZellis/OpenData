/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.plugin;

import com.towermarsh.opendata.plugin.PluginDescriptor;
import com.towermarsh.opendata.plugin.PluginMetrics;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * Verifies parallel plugin execution, failure isolation and cancellation.
 *
 * @author Terry Curran
 * @since 2.1.0
 * @version 4.1.0
 */
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
            /**
 * @author Terry Curran
 * @version 1.0.0
 */
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


    /**
     * Interrupting the caller must also interrupt the active plugin worker,
     * rather than leaving it running after the coordinator exits.
     */
    @Test
    void interruptsActivePluginAndPropagatesCallerCancellation() throws Exception {
        final var entered = new CountDownLatch(1);
        final var cancelled = new CountDownLatch(1);
        final var release = new CountDownLatch(1);
        final var failure = new AtomicReference<Throwable>();
        final var interruptedFlag = new AtomicBoolean();
        final PluginFactory factory = ignored -> context -> {
            entered.countDown();
            try {
                release.await(5, TimeUnit.SECONDS);
                return PluginMetrics.ZERO;
            } catch (InterruptedException exception) {
                cancelled.countDown();
                throw exception;
            }
        };
        final DatabaseResourceManager database =
                org.mockito.Mockito.mock(DatabaseResourceManager.class);
        final var coordinator = new PluginExecutionCoordinator(
                factory, new NoOpPluginRunAudit(), database,
                Clock.systemUTC(), Duration.ofSeconds(2));
        final var caller = new Thread(() -> {
            try {
                coordinator.execute(List.of(plugin("slow")), 1, true);
            } catch (Throwable exception) {
                failure.set(exception);
                interruptedFlag.set(Thread.currentThread().isInterrupted());
            }
        }, "plugin-execution-test-caller");
        caller.setDaemon(true);
        try {
            caller.start();
            assertTrue(entered.await(3, TimeUnit.SECONDS), "Plugin did not start");
            caller.interrupt();
            caller.join(3_000);
            assertFalse(caller.isAlive(), "Coordinator did not exit after interruption");
            assertInstanceOf(InterruptedException.class, failure.get());
            assertTrue(interruptedFlag.get(), "Caller interruption flag was not preserved");
            assertTrue(cancelled.await(3, TimeUnit.SECONDS), "Active plugin was not interrupted");
        } finally {
            release.countDown();
            caller.interrupt();
            caller.join(3_000);
        }
    }

    /** Rejects a shutdown timeout that cannot be used for a meaningful wait. */
    @Test
    void rejectsNonPositiveShutdownTimeout() {
        final DatabaseResourceManager database =
                org.mockito.Mockito.mock(DatabaseResourceManager.class);
        final PluginFactory factory = ignored -> context -> PluginMetrics.ZERO;
        assertThrows(IllegalArgumentException.class, () ->
                new PluginExecutionCoordinator(factory, new NoOpPluginRunAudit(),
                        database, Clock.systemUTC(), Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () ->
                new PluginExecutionCoordinator(factory, new NoOpPluginRunAudit(),
                        database, Clock.systemUTC(), Duration.ofMillis(-1)));
    }

    private static ResolvedPlugin plugin(final String id) {
        final var descriptor = new PluginDescriptor(id, id, "", "example." + id, true, 1);
        final var definition = new PluginDefinition(
                id, id, "", "example." + id, true, 1, id, List.of(), Map.of(), Map.of());
        return new ResolvedPlugin(descriptor, definition);
    }
}
