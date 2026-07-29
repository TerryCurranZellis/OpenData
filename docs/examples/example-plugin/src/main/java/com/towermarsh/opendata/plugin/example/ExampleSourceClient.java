/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** Minimal HTTP extraction template. Add retries and source-specific validation as required. */
public final class ExampleSourceClient {
    private final HttpClient client = HttpClient.newHttpClient();

    public String download(final ExamplePluginConfiguration configuration) throws Exception {
        final HttpRequest request = HttpRequest.newBuilder(configuration.endpoint())
                .timeout(configuration.requestTimeout())
                .GET()
                .build();
        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Example source returned HTTP " + response.statusCode());
        }
        return response.body();
    }
}
