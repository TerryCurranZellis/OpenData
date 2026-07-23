/*
 * Filename: PropertiesPluginDefinitionLoaderTest.java
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

package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.towermarsh.opendata.config.model.DatasetFormat;
import com.towermarsh.opendata.config.model.DownloadStrategyType;

class PropertiesPluginDefinitionLoaderTest {

    @Test
    void loadsStructuredOfgemDefinition() {
        final var loader = new PropertiesPluginDefinitionLoader();
        final var definition = loader.load("ofgem", Map.of());

        assertEquals("ofgem", definition.id());
        assertEquals("ofgem-energy-price-cap", definition.datasetId());
        assertEquals(1, definition.endpoints().size());

        final var endpoint =
                definition.requireEndpoint("price-cap-publication");

        assertEquals(DatasetFormat.HTML, endpoint.contentFormat());
        assertEquals(
                DownloadStrategyType.HTML_LINK_DISCOVERY,
                endpoint.strategy());
        assertTrue(endpoint.linkDiscovery().isPresent());
        assertEquals(
                "dbo",
                definition.requireProperty("database.target-schema"));
    }

    @Test
    void runtimeOverrideChangesStructuredProperty() {
        final var loader = new PropertiesPluginDefinitionLoader();
        final var definition = loader.load(
                "ofgem",
                Map.of(
                        "property.database.target-schema.value",
                        "stage"));

        assertEquals(
                "stage",
                definition.requireProperty("database.target-schema"));
    }
}
