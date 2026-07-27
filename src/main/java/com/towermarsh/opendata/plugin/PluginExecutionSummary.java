/*
 * Filename: PluginExecutionSummary.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import java.util.List;
import java.util.Objects;

/** Aggregate result of one multi-plugin invocation. */
/**
 * Aggregate result of one multi-plugin invocation.
 *
 * @param results completed results for the selected plugins
 */
public record PluginExecutionSummary(List<PluginRunResult> results) {
    /** Validates and normalises record components. */
    public PluginExecutionSummary {
        results = List.copyOf(Objects.requireNonNull(results, "results"));
    }

    /**
     * Indicates whether every selected plugin completed successfully.
     *
     * @return {@code true} when all plugin runs succeeded or were dry runs
     */
    public boolean allSuccessful() {
        return results.stream().allMatch(PluginRunResult::successful);
    }

    /**
     * Returns the number of successful plugin runs.
     *
     * @return successful plugin run count
     */
    public long succeeded() {
        return results.stream().filter(PluginRunResult::successful).count();
    }

    /**
     * Returns the number of failed or cancelled plugin runs.
     *
     * @return failed or cancelled plugin run count
     */
    public long failed() {
        return results.size() - succeeded();
    }
}
