/*
 * Filename: OpenMeteoPlugin.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.openmeteo.config.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.download.OpenMeteoDownloader;
import com.towermarsh.opendata.plugin.openmeteo.exception.OpenMeteoException;
import com.towermarsh.opendata.plugin.openmeteo.extract.OpenMeteoResponseExtractor;
import com.towermarsh.opendata.plugin.openmeteo.load.OpenMeteoRepository;
import com.towermarsh.opendata.plugin.openmeteo.transform.OpenMeteoTransformer;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import com.towermarsh.opendata.plugin.openmeteo.transform.validate.OpenMeteoResponseValidator;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/** Complete Open-Meteo download and SQL Server persistence plugin. */
public final class OpenMeteoPlugin implements OpenDataPlugin {
    public static final String PLUGIN_ID = "openmeteo";
    private static final Logger LOGGER = Logger.getLogger(OpenMeteoPlugin.class.getName());

    private final OpenMeteoConfiguration configuration;
    private final OpenMeteoDownloader downloader;
    private final OpenMeteoResponseExtractor extractor;
    private final OpenMeteoResponseValidator validator;
    private final OpenMeteoTransformer transformer;

    /**
     * Creates the Open-Meteo plugin from a resolved plugin definition.
     *
     * @param definition resolved plugin definition
     */
    public OpenMeteoPlugin(final PluginDefinition definition) {
        this(OpenMeteoConfiguration.from(definition));
    }

    /**
     * Creates the Open-Meteo plugin from typed configuration.
     *
     * @param configuration typed Open-Meteo configuration
     */
    public OpenMeteoPlugin(final OpenMeteoConfiguration configuration) {
        this(
                configuration,
                new OpenMeteoDownloader(configuration),
                new OpenMeteoResponseExtractor(),
                new OpenMeteoResponseValidator(),
                new OpenMeteoTransformer());
    }

    OpenMeteoPlugin(
            final OpenMeteoConfiguration configuration,
            final OpenMeteoDownloader downloader,
            final OpenMeteoResponseExtractor extractor,
            final OpenMeteoResponseValidator validator,
            final OpenMeteoTransformer transformer) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.downloader = Objects.requireNonNull(downloader, "downloader");
        this.extractor = Objects.requireNonNull(extractor, "extractor");
        this.validator = Objects.requireNonNull(validator, "validator");
        this.transformer = Objects.requireNonNull(transformer, "transformer");
    }

    @Override
    /**
     * Executes the complete Open-Meteo pipeline for one plugin task.
     *
     * @param context plugin execution context
     * @return plugin metrics
     * @throws OpenMeteoException if the pipeline fails
     */
    public PluginMetrics execute(final PluginExecutionContext context) throws OpenMeteoException {
        Objects.requireNonNull(context, "context");
        final List<DailyWeatherRecord> records = execute();
        LOGGER.info(() -> "OpenMeteo produced %d daily records".formatted(records.size()));
        if (context.dryRun()) {
            return new PluginMetrics(records.size(), 0, 0, records.size());
        }
        final var persistence = new OpenMeteoRepository(context.database())
                .save(configuration, records, context.runId());
        return new PluginMetrics(
                records.size(),
                persistence.inserted(),
                persistence.updated(),
                persistence.skipped());
    }

    /** Runs the download, extract, validate, and transform stages without loading. */
    public List<DailyWeatherRecord> execute() throws OpenMeteoException {
        return process(downloader.download());
    }

    /**
     * Runs the download, extract, validate, and transform stages for an explicit date range.
     *
     * @param startDate inclusive start date
     * @param endDate inclusive end date
     * @return transformed daily weather records
     * @throws OpenMeteoException if processing fails
     */
    public List<DailyWeatherRecord> execute(
            final LocalDate startDate,
            final LocalDate endDate) throws OpenMeteoException {
        return process(downloader.download(startDate, endDate));
    }

    /**
     * Validates and transforms raw Open-Meteo JSON.
     *
     * @param json raw JSON payload
     * @return transformed daily weather records
     * @throws OpenMeteoException if extraction or validation fails
     */
    private List<DailyWeatherRecord> process(final String json) throws OpenMeteoException {
        final var response = validator.validate(extractor.extract(json));
        return transformer.transform(response, configuration);
    }

    /**
     * Returns the typed Open-Meteo configuration.
     *
     * @return typed Open-Meteo configuration
     */
    public OpenMeteoConfiguration configuration() {
        return configuration;
    }
}
