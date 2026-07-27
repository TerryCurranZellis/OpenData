/*
 * Filename: PluginRunAudit.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import java.time.Instant;
import java.util.UUID;

/** Persists generic plugin-run lifecycle information. */
public interface PluginRunAudit {

    /**
     *
     * @param runId
     * @param pluginId
     * @param threadName
     * @param startedAt
     */
    void started(UUID runId, String pluginId, String threadName, Instant startedAt);

    /**
     *
     * @param result
     */
    void completed(PluginRunResult result);
}
