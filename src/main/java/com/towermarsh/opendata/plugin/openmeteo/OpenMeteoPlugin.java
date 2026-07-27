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

    /**
     *
     */
    public static final String PLUGIN_ID = "openmeteo";
    private static final Logger LOGGER = Logger.getLogger(OpenMeteoPlugin.class.getName());

    private final OpenMeteoConfiguration configuration;
    private final OpenMeteoDownloader downloader;
    private final OpenMeteoResponseExtractor extractor;
    private final OpenMeteoResponseValidator validator;
    private final OpenMeteoTransformer transformer;

    /**
     *
     * @param definition
     */
    public OpenMeteoPlugin(final PluginDefinition definition) {
        this(OpenMeteoConfiguration.from(definition));
    }

    /**
     *
     * @param configuration
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

    /**
     *
     * @param context
     * @return
     * @throws OpenMeteoException
     */
    @Override
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

    /** Runs the download, extract, validate, and transform stages without loading.
     * @return
     * @throws com.towermarsh.opendata.plugin.openmeteo.exception.OpenMeteoException  */
    public List<DailyWeatherRecord> execute() throws OpenMeteoException {
        return process(downloader.download());
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws OpenMeteoException
     */
    public List<DailyWeatherRecord> execute(
            final LocalDate startDate,
            final LocalDate endDate) throws OpenMeteoException {
        return process(downloader.download(startDate, endDate));
    }

    private List<DailyWeatherRecord> process(final String json) throws OpenMeteoException {
        final var response = validator.validate(extractor.extract(json));
        return transformer.transform(response, configuration);
    }

    /**
     *
     * @return
     */
    public OpenMeteoConfiguration configuration() {
        return configuration;
    }
}
