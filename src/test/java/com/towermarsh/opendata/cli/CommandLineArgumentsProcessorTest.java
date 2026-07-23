/*
 * Filename: CommandLineArgumentsProcessorTest.java
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

package com.towermarsh.opendata.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class CommandLineArgumentsProcessorTest {

    private final CommandLineArgumentsProcessor processor =
            new CommandLineArgumentsProcessor();

    @Test
    void parsesPluginAndOverrideFile() {
        final CommandLineArguments arguments = processor.parse(new String[] {
                "--plugin", "Ofgem",
                "--file", "settings.properties",
                "--dry-run",
                "--verbose"
        });

        assertEquals("ofgem", arguments.pluginId().orElseThrow());
        assertEquals(
                Path.of("settings.properties").toAbsolutePath().normalize(),
                arguments.overrideFile().orElseThrow());
        assertTrue(arguments.dryRun());
        assertTrue(arguments.verbose());
        assertFalse(arguments.isInformationalRequest());
    }

    @Test
    void allowsHelpWithoutPlugin() {
        final CommandLineArguments arguments =
                processor.parse(new String[] {"--help"});

        assertTrue(arguments.helpRequested());
        assertTrue(arguments.isInformationalRequest());
    }

    @Test
    void rejectsNormalInvocationWithoutPlugin() {
        assertThrows(
                CommandLineProcessingException.class,
                () -> processor.parse(new String[] {"--dry-run"}));
    }

    @Test
    void rejectsFileWithoutPlugin() {
        assertThrows(
                CommandLineProcessingException.class,
                () -> processor.parse(new String[] {"--file", "settings.properties"}));
    }
}
