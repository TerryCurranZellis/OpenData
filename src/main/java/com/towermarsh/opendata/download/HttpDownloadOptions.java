/*
 * Filename: HttpDownloadOptions.java
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
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record HttpDownloadOptions(
        Duration connectTimeout,
        Duration requestTimeout,
        String userAgent,
        boolean overwrite,
        long maximumBytes) {

    /** Validates and normalises record components. */

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
