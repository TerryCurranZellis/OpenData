/*
 * Filename: BootstrapConfig.java
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

package com.towermarsh.opendata.config.model;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * Application settings required before a plugin definition can be loaded.
 *
 * @param applicationName application name
 * @param environment runtime environment name
 * @param workingDirectory working directory
 * @param archiveDirectory archive directory
 * @param failedDirectory failed-download directory
 * @param values additional bootstrap values
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public record BootstrapConfig(
        String applicationName,
        String environment,
        Path workingDirectory,
        Path archiveDirectory,
        Path failedDirectory,
        Map<String, String> values) {

    public BootstrapConfig {
        Objects.requireNonNull(applicationName, "applicationName");
        Objects.requireNonNull(environment, "environment");
        Objects.requireNonNull(workingDirectory, "workingDirectory");
        Objects.requireNonNull(archiveDirectory, "archiveDirectory");
        Objects.requireNonNull(failedDirectory, "failedDirectory");
        values = Map.copyOf(Objects.requireNonNull(values, "values"));
    }
}
