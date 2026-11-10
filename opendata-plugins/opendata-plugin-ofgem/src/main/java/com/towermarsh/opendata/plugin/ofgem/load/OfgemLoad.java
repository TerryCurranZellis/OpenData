/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.ofgem.load;

import com.towermarsh.opendata.download.strategy.ResolvedDownload;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapWorkbookData;
import java.util.Objects;

/**
 * Loads transformed Ofgem records into the configured database tables.
 * @author Terry Curran
 * @version 4.1.0
 */
public final class OfgemLoad {

    /**
     * Persists validated price-cap records or counts them without writes in dry-run mode.
     *
     * @param context current plugin execution context
     * @param download resolved source workbook and provenance
     * @param data validated price-cap records
     * @return read, inserted, updated and skipped row counts
     */
    public PluginMetrics load(PluginExecutionContext context,
            ResolvedDownload download,
            OfgemPriceCapWorkbookData data) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(download, "download");
        Objects.requireNonNull(data, "data");
        var read = data.levels().size();
        if (context.dryRun()) return new PluginMetrics(read, 0, 0, read);
        var result = new OfgemPersistenceRepository(context.database())
                .persist(context.definition(), download, data);
        return new PluginMetrics(read, result.inserted(), result.updated(), result.skipped());
    }
}
