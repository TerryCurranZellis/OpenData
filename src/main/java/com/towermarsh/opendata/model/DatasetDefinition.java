/*
 * Filename: DatasetDefinition.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.model;

import java.util.Objects;

/**
 * Defines an OpenData dataset.
 *
 *
 * @author Terry Curran
 * @version 21 Jul 2026
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

    public String getDatasetId() {
        return datasetId;
    }

    public String getName() {
        return name;
    }

    public DataSourceDefinition getSource() {
        return source;
    }
}
