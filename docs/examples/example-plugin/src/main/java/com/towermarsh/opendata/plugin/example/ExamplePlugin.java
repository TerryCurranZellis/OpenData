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
import java.util.List;
import java.util.Objects;

/** Compact example of the current executable plugin contract. */
public final class ExamplePlugin implements OpenDataPlugin {

    public static final String PLUGIN_ID = "example";

    private final ExamplePluginConfiguration configuration;
    private final ExampleSourceClient sourceClient;
    private final ExampleTransformer transformer;

    /** Constructor used by {@code ReflectionPluginFactory}. */
    public ExamplePlugin(final PluginDefinition definition) {
        this(
                ExamplePluginConfiguration.from(definition),
                new ExampleSourceClient(),
                new ExampleTransformer());
    }

    ExamplePlugin(
            final ExamplePluginConfiguration configuration,
            final ExampleSourceClient sourceClient,
            final ExampleTransformer transformer) {
        this.configuration = Objects.requireNonNull(
                configuration, "configuration");
        this.sourceClient = Objects.requireNonNull(
                sourceClient, "sourceClient");
        this.transformer = Objects.requireNonNull(
                transformer, "transformer");
    }

    @Override
    public PluginMetrics execute(final PluginExecutionContext context)
            throws Exception {
        Objects.requireNonNull(context, "context");
        final String payload = sourceClient.download(configuration);
        final List<ExampleRecord> records =
                transformer.transform(payload);

        if (context.dryRun()) {
            return new PluginMetrics(
                    records.size(), 0, 0, records.size());
        }

        final var result = new ExampleRepository(context.database())
                .save(records, context.runId());
        return new PluginMetrics(
                records.size(),
                result.inserted(),
                result.updated(),
                result.skipped());
    }
}
