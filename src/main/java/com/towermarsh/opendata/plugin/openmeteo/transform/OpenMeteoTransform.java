/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.openmeteo.transform;

import com.towermarsh.opendata.plugin.openmeteo.initialise.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import com.towermarsh.opendata.plugin.openmeteo.transform.validate.OpenMeteoResponseValidator;
import java.util.List;
import java.util.Objects;

/** Converts downloaded Open-Meteo JSON into validated database records. */
public final class OpenMeteoTransform {
    private final OpenMeteoConfiguration configuration;
    private final OpenMeteoResponseExtractor extractor = new OpenMeteoResponseExtractor();
    private final OpenMeteoResponseValidator validator = new OpenMeteoResponseValidator();
    private final OpenMeteoTransformer transformer = new OpenMeteoTransformer();

    /**
     *
     * @param configuration
     */
    public OpenMeteoTransform(final OpenMeteoConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
    }

    /**
     *
     * @param downloadedData
     * @return
     */
    public List<DailyWeatherRecord> transform(final String downloadedData) {
        return transformer.transform(validator.validate(extractor.extract(downloadedData)), configuration);
    }
}
