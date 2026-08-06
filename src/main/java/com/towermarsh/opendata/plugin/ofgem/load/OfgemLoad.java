/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.ofgem.load;

import com.towermarsh.opendata.download.strategy.ResolvedDownload;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.ofgem.transform.model.OfgemPriceCapWorkbookData;
import java.util.Objects;

/** Loads transformed Ofgem records into the configured database tables. */
public final class OfgemLoad {

    /**
     *
     * @param context
     * @param download
     * @param data
     * @return
     */
    public PluginMetrics load(final PluginExecutionContext context,
            final ResolvedDownload download,
            final OfgemPriceCapWorkbookData data) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(download, "download");
        Objects.requireNonNull(data, "data");
        final int read = data.levels().size();
        if (context.dryRun()) return new PluginMetrics(read, 0, 0, read);
        final OfgemPersistenceResult result = new OfgemPersistenceRepository(context.database())
                .persist(context.definition(), download, data);
        return new PluginMetrics(read, result.inserted(), result.updated(), result.skipped());
    }
}
