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
public record PluginExecutionSummary(List<PluginRunResult> results) {
    public PluginExecutionSummary {
        results = List.copyOf(Objects.requireNonNull(results, "results"));
    }

    public boolean allSuccessful() {
        return results.stream().allMatch(PluginRunResult::successful);
    }

    public long succeeded() {
        return results.stream().filter(PluginRunResult::successful).count();
    }

    public long failed() {
        return results.size() - succeeded();
    }
}
