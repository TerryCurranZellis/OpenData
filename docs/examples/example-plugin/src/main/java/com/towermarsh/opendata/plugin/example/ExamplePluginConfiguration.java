package com.towermarsh.opendata.plugin.example;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.net.URI;
import java.time.Duration;

/** Typed configuration template derived from a plugin definition. */
public record ExamplePluginConfiguration(URI endpoint, Duration requestTimeout, String targetSchema) {
    public static ExamplePluginConfiguration from(final PluginDefinition definition) {
        final URI endpoint = definition.requireEndpoint("source").uri();
        final int timeoutSeconds = Integer.parseInt(definition.requireProperty("request-timeout-seconds"));
        final String targetSchema = definition.requireProperty("database.target-schema");
        return new ExamplePluginConfiguration(endpoint, Duration.ofSeconds(timeoutSeconds), targetSchema);
    }
}
