/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

/** Minimal HTTP extraction example; production code needs source-specific limits. */
public final class ExampleSourceClient {

    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public String download(
            final ExamplePluginConfiguration configuration)
            throws Exception {
        Objects.requireNonNull(configuration, "configuration");
        final var request = HttpRequest.newBuilder(
                configuration.endpoint())
                .timeout(configuration.requestTimeout())
                .GET()
                .build();

        final var response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "Example source returned HTTP "
                    + response.statusCode());
        }
        return response.body();
    }
}
