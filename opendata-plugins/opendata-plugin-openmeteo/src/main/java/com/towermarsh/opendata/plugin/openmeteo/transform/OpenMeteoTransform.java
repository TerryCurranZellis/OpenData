/* 
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0 
 */
package com.towermarsh.opendata.plugin.openmeteo.transform;

import com.towermarsh.opendata.plugin.openmeteo.initialise.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import com.towermarsh.opendata.plugin.openmeteo.transform.validate.OpenMeteoResponseValidator;
import java.util.List;
import java.util.Objects;

/**
 * Converts downloaded Open-Meteo JSON into validated database records.
 * @author Terry Curran
 * @version 4.1.0
 */
public final class OpenMeteoTransform {

    private final OpenMeteoConfiguration configuration;
    private final OpenMeteoResponseExtractor extractor = new OpenMeteoResponseExtractor();
    private final OpenMeteoResponseValidator validator = new OpenMeteoResponseValidator();
    private final OpenMeteoTransformer transformer = new OpenMeteoTransformer();

    /**
     * Creates a transformer for the requested weather extraction period.
     *
     * @param configuration validated Open-Meteo plugin configuration
     */
    public OpenMeteoTransform(OpenMeteoConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
    }

    /**
     * Parses, validates and transforms a downloaded Open-Meteo JSON response.
     *
     * @param downloadedData downloaded API response body
     * @return validated daily weather observations
     */
    public List<DailyWeatherRecord> transform(String downloadedData) {
        return transformer.transform(validator.validate(extractor.extract(downloadedData)), configuration);
    }
}
