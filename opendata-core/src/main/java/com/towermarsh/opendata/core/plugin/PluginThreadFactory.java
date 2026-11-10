/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.plugin;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates named, non-daemon worker threads for plugin execution.
 *
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 */
public class PluginThreadFactory implements ThreadFactory {
    private final AtomicInteger sequence = new AtomicInteger();

    /**
     * Creates a new worker thread with a unique name and an exception logger.
     *
     * @param task work to execute on the new thread
     * @return a new, not-yet-started non-daemon thread
     */
    @Override
    public Thread newThread(Runnable task) {
        var thread = new Thread(task, "opendata-plugin-" + sequence.incrementAndGet());
        thread.setDaemon(false);
        thread.setUncaughtExceptionHandler((worker, error) ->
                java.util.logging.Logger.getLogger(PluginThreadFactory.class.getName())
                        .log(java.util.logging.Level.SEVERE,
                                "Uncaught exception in " + worker.getName(), error));
        return thread;
    }
}
