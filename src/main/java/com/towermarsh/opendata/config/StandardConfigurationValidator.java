/*
 * Filename: StandardConfigurationValidator.java
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

import java.time.Duration;

import com.towermarsh.opendata.config.model.BootstrapConfig;
import com.towermarsh.opendata.exception.ConfigurationException;

/**
 * Performs framework-level validation common to every dataset plugin.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class StandardConfigurationValidator implements ConfigurationValidator {

    /**
     * validate the configuration
     *
     * @param configuration configuration to validate
     */
    @Override
    public void validate(final ApplicationConfig configuration) {
        final BootstrapConfig bootstrap = configuration.bootstrap();

        final var connectTimeoutStr = bootstrap.values().get("http.connect-timeout-seconds");
        if (connectTimeoutStr != null) {
            try {
                final var connectTimeoutSeconds = Integer.parseInt(connectTimeoutStr.trim());
                if (connectTimeoutSeconds <= 0) {
                    throw new ConfigurationException(
                            "http.connect-timeout-seconds must be greater than zero.");
                }
            } catch (NumberFormatException exception) {
                throw new ConfigurationException(
                        "Invalid value for http.connect-timeout-seconds: " + connectTimeoutStr,
                        exception);
            }
        }

        final var requestTimeoutStr = bootstrap.values().get("http.request-timeout-seconds");
        if (requestTimeoutStr != null) {
            try {
                final var requestTimeoutSeconds = Integer.parseInt(requestTimeoutStr.trim());
                if (requestTimeoutSeconds <= 0) {
                    throw new ConfigurationException(
                            "http.request-timeout-seconds must be greater than zero.");
                }
            } catch (NumberFormatException exception) {
                throw new ConfigurationException(
                        "Invalid value for http.request-timeout-seconds: " + requestTimeoutStr,
                        exception);
            }
        }

        final var workingDirectory = bootstrap.workingDirectory();
        if (workingDirectory.toString().isBlank()) {
            throw new ConfigurationException("application.working-directory must not be blank.");
        }

        final var lockTimeoutStr = bootstrap.values().get("pipeline.lock-timeout");
        if (lockTimeoutStr != null) {
            try {
                Duration.parse(lockTimeoutStr.trim());
            } catch (java.time.format.DateTimeParseException exception) {
                throw new ConfigurationException(
                        "Invalid duration for pipeline.lock-timeout: " + lockTimeoutStr,
                        exception);
            }
        }
    }
}
