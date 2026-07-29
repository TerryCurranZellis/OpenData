/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.example.config.ExampleConfiguration;
import com.towermarsh.opendata.plugin.example.download.ExampleDownloader;
import com.towermarsh.opendata.plugin.example.extract.ExampleExtractor;
import com.towermarsh.opendata.plugin.example.load.ExampleLoader;
import com.towermarsh.opendata.plugin.example.transform.ExampleTransformer;
import com.towermarsh.opendata.plugin.example.transform.validate.ExampleValidator;
import java.util.Objects;

/** Workflow facade for one example-plugin execution. */
public final class ExamplePlugin implements OpenDataPlugin {
    private final ExampleConfiguration configuration;

    public ExamplePlugin(final PluginDefinition definition) {
        this(ExampleConfiguration.from(definition));
    }

    public ExamplePlugin(final ExampleConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
    }

    @Override
    public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        Objects.requireNonNull(context, "context");

        final var source = new ExampleDownloader(configuration).download();
        final var extracted = new ExampleExtractor().extract(source);
        final var records = new ExampleValidator().validate(
                new ExampleTransformer().transform(extracted));

        if (context.dryRun()) {
            return new PluginMetrics(records.size(), 0, 0, records.size());
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
