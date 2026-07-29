/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.model;

import java.util.Objects;

/**
 * Defines an OpenData source.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class DataSourceDefinition {

    private final String name;
    private final String description;
    private final String url;

    /**
     * Creates a data source definition.
     *
     * @param name source name
     * @param description source description
     * @param url source URL
     */
    public DataSourceDefinition(
            String name,
            String description,
            String url) {

        this.name
                = Objects.requireNonNull(
                        name,
                        "name");

        this.description
                = Objects.requireNonNull(
                        description,
                        "description");

        this.url
                = Objects.requireNonNull(
                        url,
                        "url");
    }

    /**
     * Returns the source name.
     *
     * @return source name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the source description.
     *
     * @return source description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the source URL.
     *
     * @return source URL
     */
    public String getUrl() {
        return url;
    }
}
