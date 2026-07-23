/*
 * Filename: ApplicationConfigTest.java
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

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.towermarsh.opendata.config.model.BootstrapConfig;

class ApplicationConfigTest {

    @Test
    void exposesSelectedPluginIdentifier() {
        final var plugin =
                new PropertiesPluginDefinitionLoader().load("ofgem", Map.of());

        final var config = new ApplicationConfig(
                new BootstrapConfig(
                        "OpenData",
                        "test",
                        Path.of("work"),
                        Path.of("archive"),
                        Path.of("failed"),
                        Map.of()),
                plugin,
                Map.of(),
                Optional.empty(),
                true,
                false);

        assertEquals("ofgem", config.pluginId());
    }
}
