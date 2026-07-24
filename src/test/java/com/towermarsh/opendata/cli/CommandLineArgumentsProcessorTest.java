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
