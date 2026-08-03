/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.initialise;

import com.towermarsh.opendata.model.DataFile;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.example.config.ExampleConfiguration;
import com.towermarsh.opendata.plugin.example.download.ExampleDownloader;
import com.towermarsh.opendata.plugin.example.extract.ExampleExtractor;
import com.towermarsh.opendata.plugin.example.finalise.ExampleFinalise;
import com.towermarsh.opendata.plugin.example.load.ExampleLoad;
import com.towermarsh.opendata.plugin.example.transform.ExampleTransformer;
import com.towermarsh.opendata.plugin.example.transform.model.ExampleRecord;
import com.towermarsh.opendata.plugin.example.transform.validate.ExampleValidator;
import java.util.List;
import java.util.Objects;

/** Creates stage objects and controls the complete example lifecycle. */
public final class ExampleInitialise {

    private final ExampleExtractor extract;
    private final ExampleTransformer transform;
    private final ExampleValidator validator;
    private final ExampleLoad load;
    private final ExampleFinalise finalise;

    public ExampleInitialise(final ExampleConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration");
        this.extract = new ExampleExtractor(
                new ExampleDownloader(configuration));
        this.transform = new ExampleTransformer();
        this.validator = new ExampleValidator();
        this.load = new ExampleLoad();
        this.finalise = new ExampleFinalise();
    }

    public PluginMetrics execute(final PluginExecutionContext context)
            throws Exception {
        Objects.requireNonNull(context, "context");
        DataFile source = null;
        List<ExampleRecord> records = List.of();
        PluginMetrics metrics = PluginMetrics.ZERO;
        boolean completed = false;
        try {
            source = extract.extract();
            records = validator.validate(transform.transform(source));
            metrics = load.load(records, context);
            completed = true;
            return metrics;
        } finally {
            finalise.complete(
                    context, source, records, metrics, completed);
        }
    }
}
