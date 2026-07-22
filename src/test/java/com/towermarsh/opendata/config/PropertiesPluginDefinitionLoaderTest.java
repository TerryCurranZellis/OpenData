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
