/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.neso;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Typed configuration shared by the NESO electricity plugins.
 *
 * @param startDate inclusive first observation date
 * @param endDate inclusive last observation date
 * @param baseUrl CKAN datapackage_show URL
 * @param datasetId CKAN dataset slug
 * @param connectTimeout HTTP connect timeout
 * @param requestTimeout HTTP request timeout
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public record NesoSettings(
        LocalDate startDate,
        LocalDate endDate,
        String baseUrl,
        String datasetId,
        Duration connectTimeout,
        Duration requestTimeout) {

    /** Validates the settings. */
    public NesoSettings {
        Objects.requireNonNull(startDate, "startDate");
        Objects.requireNonNull(endDate, "endDate");
        Objects.requireNonNull(baseUrl, "baseUrl");
        Objects.requireNonNull(datasetId, "datasetId");
        Objects.requireNonNull(connectTimeout, "connectTimeout");
        Objects.requireNonNull(requestTimeout, "requestTimeout");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("end-date precedes start-date");
        }
        if (!baseUrl.startsWith("https://")) {
            throw new IllegalArgumentException("api.base-url must be an HTTPS URL: " + baseUrl);
        }
    }

    /**
     * Reads the settings from a plugin definition.
     *
     * @param definition plugin definition
     * @param defaultDatasetId dataset slug used when none is configured
     * @return typed settings
     */
    public static NesoSettings from(PluginDefinition definition, String defaultDatasetId) {
        Objects.requireNonNull(definition, "definition");
        return new NesoSettings(
                LocalDate.parse(property(definition, "start-date", "2009-01-01")),
                LocalDate.parse(property(definition, "end-date", "2025-12-31")),
                property(definition, "api.base-url",
                        "https://api.neso.energy/api/3/action/datapackage_show"),
                property(definition, "api.dataset-id", defaultDatasetId),
                Duration.ofSeconds(Long.parseLong(property(definition, "http.connect-timeout-seconds", "30"))),
                Duration.ofSeconds(Long.parseLong(property(definition, "http.request-timeout-seconds", "180"))));
    }

    /**
     * Property.
     *
     * @param definition input definition
     * @param name requested name
     * @param defaultValue value of default value
     * @return result of property
     */
    private static String property(
            PluginDefinition definition, String name, String defaultValue) {
        return definition.findProperty(name)
                .map(p -> p.value())
                .filter(v -> !v.isBlank())
                .orElse(defaultValue);
    }
}
