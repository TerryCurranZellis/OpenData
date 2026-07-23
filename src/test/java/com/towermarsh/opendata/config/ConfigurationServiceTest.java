/*
 * Filename: ConfigurationServiceTest.java
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

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.towermarsh.opendata.cli.CommandLineArguments;

class ConfigurationServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void overrideFileTakesPrecedenceOverPluginDefaults() throws Exception {
        final Path overrideFile = temporaryDirectory.resolve("ofgem.properties");
        Files.writeString(overrideFile, """
                database.batch-size=250
                application.working-directory=local-work
                """);

        final CommandLineArguments arguments = CommandLineArguments.builder()
                .pluginId("ofgem")
                .overrideFile(overrideFile)
                .dryRun(true)
                .build();

        final ApplicationConfig configuration =
                new ConfigurationService().resolve(arguments);

        assertEquals("ofgem", configuration.pluginId());
        assertEquals(
                250,
                Integer.parseInt(configuration.bootstrap().values().get("database.batch-size")));
        assertEquals(Path.of("local-work"), configuration.bootstrap().workingDirectory());
        assertTrue(configuration.dryRun());
    }
}
