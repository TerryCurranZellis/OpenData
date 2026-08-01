/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/** Names non-daemon plugin worker threads for logs and diagnostics.  *
* @author Terry Curran
* @version 1.0.0
*/
public final class PluginThreadFactory implements ThreadFactory {
    private final AtomicInteger sequence = new AtomicInteger();

    /**
     *
     * @param task
     * @return
     */
    @Override
    public Thread newThread(final Runnable task) {
        final Thread thread = new Thread(task, "opendata-plugin-" + sequence.incrementAndGet());
        thread.setDaemon(false);
        thread.setUncaughtExceptionHandler((worker, error) ->
                java.util.logging.Logger.getLogger(PluginThreadFactory.class.getName())
                        .log(java.util.logging.Level.SEVERE,
                                "Uncaught exception in " + worker.getName(), error));
        return thread;
    }
}
