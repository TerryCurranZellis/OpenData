/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

/**
 * Row counts returned by a plugin execution.
 *
 * @param read number of source rows or records read
 * @param inserted number of rows inserted
 * @param updated number of rows updated
 * @param skipped number of rows skipped
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record PluginMetrics(long read, long inserted, long updated, long skipped) {
    public static final PluginMetrics ZERO = new PluginMetrics(0, 0, 0, 0);

    /** Validates and normalises record components. */
    public PluginMetrics {
        if (read < 0 || inserted < 0 || updated < 0 || skipped < 0) {
            throw new IllegalArgumentException("Plugin metrics must not be negative.");
        }
    }
}
