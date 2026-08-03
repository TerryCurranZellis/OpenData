/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.load;

import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.example.transform.model.ExampleRecord;
import java.util.List;
import java.util.Objects;

/** Applies dry-run policy and delegates write mode to the repository loader. */
public final class ExampleLoad {

    public PluginMetrics load(
            final List<ExampleRecord> records,
            final PluginExecutionContext context) {
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(context, "context");

        if (context.dryRun()) {
            return new PluginMetrics(
                    records.size(), 0, 0, records.size());
        }

        final var result = new ExampleLoader(context.database())
                .load(records, context.runId());
        return new PluginMetrics(
                records.size(),
                result.inserted(),
                result.updated(),
                result.skipped());
    }
}
