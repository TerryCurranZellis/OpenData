/*
 * Filename: NoOpPluginRunAudit.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import java.time.Instant;
import java.util.UUID;

/** Audit implementation for dry runs and tests.  *
* @author Terry Curran
* @version 17 July 2026
*/
public final class NoOpPluginRunAudit implements PluginRunAudit {

    /**
     *
     * @param runId
     * @param pluginId
     * @param threadName
     * @param startedAt
     */
    @Override
    public void started(final UUID runId, final String pluginId, final String threadName, final Instant startedAt) {
    }

    /**
     *
     * @param result
     */
    @Override
    public void completed(final PluginRunResult result) {
    }
}
