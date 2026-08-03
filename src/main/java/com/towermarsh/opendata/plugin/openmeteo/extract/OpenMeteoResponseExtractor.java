/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.extract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.towermarsh.opendata.exception.PluginException;
import java.util.Objects;

/**
 * Parses raw Open-Meteo JSON into the API response model.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class OpenMeteoResponseExtractor {

    private final ObjectMapper objectMapper;

    /**
     *
     * Creates an extractor backed by a default Jackson object mapper.
     *
     */
    public OpenMeteoResponseExtractor() {
        this(new ObjectMapper());
    }

    OpenMeteoResponseExtractor(final ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
    }

    /**
     * Parses a raw Open-Meteo JSON payload.
     *
     * @param json raw JSON payload
     * @return parsed API response
     * @throws com.towermarsh.opendata.exception.PluginException
     */
    public OpenMeteoResponse extract(final String json) throws PluginException {
        Objects.requireNonNull(json, "json");
        try {
            return objectMapper.readValue(json, OpenMeteoResponse.class);
        } catch (JsonProcessingException exception) {
            throw new PluginException( "Open-Meteo",
                    "Unable to parse the Open-Meteo response",
                    exception);
        }
    }
}
