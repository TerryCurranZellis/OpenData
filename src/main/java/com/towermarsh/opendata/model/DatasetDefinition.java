/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.model;

import java.util.Objects;

/**
 * Defines an OpenData dataset.
 *
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class DatasetDefinition {

    private final String datasetId;
    private final String name;
    private final DataSourceDefinition source;

    /**
     * Creates a dataset definition.
     *
     * @param datasetId unique identifier
     * @param name dataset name
     * @param source owning data source
     */
    public DatasetDefinition(
            String datasetId,
            String name,
            DataSourceDefinition source) {

        this.datasetId
                = Objects.requireNonNull(
                        datasetId,
                        "datasetId");

        this.name
                = Objects.requireNonNull(
                        name,
                        "name");

        this.source
                = Objects.requireNonNull(
                        source,
                        "source");
    }

    /**
     * Returns the dataset identifier.
     *
     * @return dataset identifier
     */
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * Returns the dataset name.
     *
     * @return dataset name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the owning data source definition.
     *
     * @return owning data source definition
     */
    public DataSourceDefinition getSource() {
        return source;
    }
}
