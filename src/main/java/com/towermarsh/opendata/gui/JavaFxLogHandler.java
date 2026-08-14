/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.logging.ContextualLogFormatter;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javafx.application.Platform;

/**
 * JUL handler that batches formatted log text onto the JavaFX application
 * thread.
 *
 * <p>Plugin execution may log concurrently from several worker threads. Each
 * record is therefore formatted on the publishing thread, placed in a
 * thread-safe queue, and drained by at most one pending
 * {@link Platform#runLater(java.lang.Runnable)} callback. This avoids one
 * JavaFX queue entry per log record during busy multi-plugin runs.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class JavaFxLogHandler extends Handler {

    private final Consumer<String> sink;
    private final ConcurrentLinkedQueue<String> pending = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean drainScheduled = new AtomicBoolean(false);
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * Creates a handler that forwards formatted batches to the supplied sink.
     *
     * @param sink JavaFX-thread consumer, normally a log TextArea append method
     */
    public JavaFxLogHandler(final Consumer<String> sink) {
        this.sink = Objects.requireNonNull(sink, "sink");
        setLevel(Level.ALL);
        setFormatter(new ContextualLogFormatter());
    }

    @Override
    public void publish(final LogRecord record) {
        if (closed.get() || !isLoggable(record)) {
            return;
        }
        try {
            pending.add(getFormatter().format(record));
            scheduleDrain();
        } catch (RuntimeException exception) {
            reportError("Unable to forward JUL record to JavaFX.",
                    exception, ErrorManager.WRITE_FAILURE);
        }
    }

    @Override
    public void flush() {
        scheduleDrain();
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            scheduleDrain();
        }
    }

    private void scheduleDrain() {
        if (pending.isEmpty() || !drainScheduled.compareAndSet(false, true)) {
            return;
        }
        try {
            Platform.runLater(this::drainOnJavaFxThread);
        } catch (IllegalStateException exception) {
            drainScheduled.set(false);
            reportError("JavaFX application thread is not available.",
                    exception, ErrorManager.GENERIC_FAILURE);
        }
    }

    private void drainOnJavaFxThread() {
        final var text = new StringBuilder();
        String next;
        while ((next = pending.poll()) != null) {
            text.append(next);
        }

        try {
            if (!text.isEmpty()) {
                sink.accept(text.toString());
            }
        } catch (RuntimeException exception) {
            reportError("Unable to append live log text.",
                    exception, ErrorManager.WRITE_FAILURE);
        } finally {
            drainScheduled.set(false);
            if (!pending.isEmpty()) {
                scheduleDrain();
            }
        }
    }
}
