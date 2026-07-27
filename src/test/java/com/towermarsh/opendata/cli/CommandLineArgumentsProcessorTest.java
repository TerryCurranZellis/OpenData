/*
 * Filename: CommandLineArgumentsProcessorTest.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CommandLineArgumentsProcessorTest {
    private final CommandLineArgumentsProcessor processor = new CommandLineArgumentsProcessor();

    @Test
    void acceptsRepeatedAndCommaSeparatedPluginsInOrder() {
        final var arguments = processor.parse(new String[]{
            "--plugin", "openmeteo,ofgem",
            "--parallelism", "2"
        });
        assertEquals(java.util.List.of("openmeteo", "ofgem"), arguments.pluginIds());
        assertEquals(2, arguments.parallelism().orElseThrow());
    }

    @Test
    void recognisesAll() {
        final var arguments = processor.parse(new String[]{"--plugin", "all"});
        assertTrue(arguments.allPluginsRequested());
        assertTrue(arguments.pluginIds().isEmpty());
    }

    @Test
    void acceptsCompleteCommandLinePassedAsOneLauncherArgument() {
        final var arguments = processor.parse(new String[]{"--plugin all --dry-run"});

        assertTrue(arguments.allPluginsRequested());
        assertTrue(arguments.dryRun());
    }

    @Test
    void preservesQuotedFilePathWhenLauncherPassesOneArgument() {
        final var arguments = processor.parse(new String[]{
            "--plugin example --file \"C:\\OpenData Files\\example.properties\" --dry-run"
        });

        assertEquals(java.nio.file.Path.of("C:\\OpenData Files\\example.properties"),
                arguments.overrideFile().orElseThrow());
        assertTrue(arguments.dryRun());
    }

    @Test
    void rejectsUnterminatedQuotedLauncherArgument() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin example --file \"C:\\OpenData Files"}));
    }

    @Test
    void rejectsDuplicatePlugins() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "openmeteo", "--plugin", "openmeteo"}));
    }

    @Test
    void rejectsAllCombinedWithNamedPlugin() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "all,openmeteo"}));
    }
}
