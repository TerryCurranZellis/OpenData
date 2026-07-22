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
