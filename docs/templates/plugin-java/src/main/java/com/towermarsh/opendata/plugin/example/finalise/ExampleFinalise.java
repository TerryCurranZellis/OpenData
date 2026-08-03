/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.finalise;

import com.towermarsh.opendata.model.DataFile;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.example.transform.model.ExampleRecord;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Performs non-destructive final reporting for the example template. */
public final class ExampleFinalise {

    private static final Logger LOGGER = Logger.getLogger(
            ExampleFinalise.class.getName());

    public void complete(
            final PluginExecutionContext context,
            final DataFile source,
            final List<ExampleRecord> records,
            final PluginMetrics metrics,
            final boolean completed) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(metrics, "metrics");

        LOGGER.log(Level.INFO,
                "Example finalise: completed={0}, source={1}, records={2}, "
                + "read={3}, inserted={4}, updated={5}, skipped={6}",
                new Object[]{
                    completed,
                    source == null ? "<none>" : source.getFilePath(),
                    records.size(),
                    metrics.read(),
                    metrics.inserted(),
                    metrics.updated(),
                    metrics.skipped()
                });

        // Add archive or cleanup only after its transaction and failure
        // semantics have been explicitly designed and tested.
    }
}
