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

/** Persists generic plugin-run lifecycle information.  *
* @author Terry Curran
* @version 17 July 2026
*/
public interface PluginRunAudit {
    /**
     * Records the start of a plugin run.
     *
     * @param runId plugin run identifier
     * @param pluginId plugin identifier
     * @param threadName worker thread name
     * @param startedAt run start time
     */
    void started(UUID runId, String pluginId, String threadName, Instant startedAt);

    /**
     * Records completion details for a plugin run.
     *
     * @param result completed plugin run result
     */
    void completed(PluginRunResult result);
}
