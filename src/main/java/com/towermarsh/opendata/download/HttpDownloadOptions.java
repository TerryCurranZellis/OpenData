/*
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.download;

import java.time.Duration;
import java.util.Objects;

/**
 * Immutable HTTP download settings.
 *
 * @param connectTimeout connection timeout
 * @param requestTimeout complete request timeout
 * @param userAgent HTTP user agent
 * @param overwrite whether an existing destination may be replaced
 * @param maximumBytes maximum accepted response size, or zero for unlimited
 */
public record HttpDownloadOptions(
        Duration connectTimeout,
        Duration requestTimeout,
        String userAgent,
        boolean overwrite,
        long maximumBytes) {

    public HttpDownloadOptions {
        Objects.requireNonNull(connectTimeout, "connectTimeout");
        Objects.requireNonNull(requestTimeout, "requestTimeout");
        Objects.requireNonNull(userAgent, "userAgent");
        if (connectTimeout.isZero() || connectTimeout.isNegative()) {
            throw new IllegalArgumentException("connectTimeout must be positive");
        }
        if (requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException("requestTimeout must be positive");
        }
        if (userAgent.isBlank()) {
            throw new IllegalArgumentException("userAgent must not be blank");
        }
        if (maximumBytes < 0) {
            throw new IllegalArgumentException("maximumBytes must be zero or positive");
        }
    }

    public static HttpDownloadOptions defaults() {
        return new HttpDownloadOptions(
                Duration.ofSeconds(20),
                Duration.ofMinutes(3),
                "OpenData/1.0 (+https://github.com/TerryCurranZellis/OpenData)",
                true,
                250L * 1024L * 1024L);
    }
}
