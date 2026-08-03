/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests OpenData command-line parsing and validation. */
class CommandLineArgumentsProcessorTest {

    private final CommandLineArgumentsProcessor processor = new CommandLineArgumentsProcessor();

    @Test
    void acceptsRepeatedAndCommaSeparatedPluginsInOrder() {
        final var arguments = processor.parse(new String[]{
            "--plugin", "openmeteo,ofgem", "--plugin", "octopus", "--parallelism", "3"
        });
        assertEquals(List.of("openmeteo", "ofgem", "octopus"), arguments.pluginIds());
        assertEquals(3, arguments.parallelism().orElseThrow());
        assertEquals(PluginCommand.RUN, arguments.command());
    }

    @Test
    void recognisesAllForRun() {
        final var arguments = processor.parse(new String[]{"--plugin", "all"});
        assertTrue(arguments.allPluginsRequested());
        assertTrue(arguments.pluginIds().isEmpty());
        assertTrue(arguments.runRequested());
    }

    @Test
    void acceptsEveryAdministrationOperationWithPluginSelection() {
        assertEquals(PluginCommand.REGISTER,
                processor.parse(new String[]{"--plugin", "ofgem", "--register"}).command());
        assertEquals(PluginCommand.UNREGISTER,
                processor.parse(new String[]{"--plugin", "ofgem", "--unregister"}).command());
        assertEquals(PluginCommand.ENABLE,
                processor.parse(new String[]{"--plugin", "ofgem", "--enable"}).command());
        assertEquals(PluginCommand.DISABLE,
                processor.parse(new String[]{"--plugin", "ofgem", "--disable"}).command());
    }

    @Test
    void acceptsAdministrationShortOptions() {
        assertTrue(processor.parse(new String[]{"-p", "ofgem", "-r"}).registerRequested());
        assertTrue(processor.parse(new String[]{"-p", "ofgem", "-u"}).unregisterRequested());
        assertTrue(processor.parse(new String[]{"-p", "ofgem", "-e"}).enableRequested());
        assertTrue(processor.parse(new String[]{"-p", "ofgem", "-d"}).disableRequested());
    }

    @Test
    void acceptsRemoveAlias() {
        final var arguments = processor.parse(new String[]{"--plugin", "all", "--remove"});
        assertTrue(arguments.unregisterRequested());
        assertTrue(arguments.allPluginsRequested());
    }

    @Test
    void acceptsPluginDefinitionFileForOneRegistration() {
        final var arguments = processor.parse(new String[]{
            "--plugin", "example", "--register", "--file", "C:\\OpenData\\example.properties"
        });
        assertEquals(Path.of("C:\\OpenData\\example.properties"),
                arguments.pluginFile().orElseThrow());
        assertTrue(arguments.registerRequested());
    }

    @Test
    void assignsDisableToDAndDryRunToN() {
        final var disable = processor.parse(new String[]{"--plugin", "ofgem", "-d"});
        assertTrue(disable.disableRequested());
        assertFalse(disable.dryRun());

        final var dryRun = processor.parse(new String[]{"--plugin", "ofgem", "-n"});
        assertEquals(PluginCommand.RUN, dryRun.command());
        assertTrue(dryRun.dryRun());
    }

    @Test
    void acceptsLongDryRun() {
        final var arguments = processor.parse(new String[]{"--plugin all --dry-run"});
        assertTrue(arguments.allPluginsRequested());
        assertTrue(arguments.dryRun());
    }

    @Test
    void acceptsInformationalOptionsWithoutPluginSelection() {
        assertTrue(processor.parse(new String[]{"--help"}).helpRequested());
        assertTrue(processor.parse(new String[]{"-a"}).aboutRequested());
        assertTrue(processor.parse(new String[]{"-l"}).listPluginsRequested());
    }

    @Test
    void preservesQuotedFilePathWhenLauncherPassesOneArgument() {
        final var arguments = processor.parse(new String[]{
            "--plugin example --register --file \"C:\\OpenData Files\\example.properties\""
        });
        assertEquals(Path.of("C:\\OpenData Files\\example.properties"),
                arguments.pluginFile().orElseThrow());
    }

    @Test
    void rejectsUnterminatedQuotedLauncherArgument() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin example --register --file \"C:\\OpenData Files"}));
    }

    @Test
    void rejectsMissingPluginForOperationalCommands() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--register"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--unregister"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--enable"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--disable"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--dry-run"}));
    }

    @Test
    void rejectsMutuallyExclusiveAdministrationOperations() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--register", "--unregister"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--enable", "--disable"}));
    }

    @Test
    void rejectsDryRunWithAdministration() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--register", "--dry-run"}));
    }

    @Test
    void rejectsInvalidFileCombinations() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--file", "ofgem.properties"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "all", "--register", "--file", "plugins.properties"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--plugin", "octopus", "--register",
                    "--file", "plugins.properties"}));
    }

    @Test
    void rejectsDuplicateAndAllCombinedPluginSelections() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "openmeteo", "--plugin", "openmeteo"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "all,openmeteo"}));
    }

    @Test
    void validatesParallelismRangeAndType() {
        assertEquals(1, processor.parse(new String[]{
            "--plugin", "ofgem", "--parallelism", "1"}).parallelism().orElseThrow());
        assertEquals(64, processor.parse(new String[]{
            "--plugin", "ofgem", "--parallelism", "64"}).parallelism().orElseThrow());
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--parallelism", "0"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--parallelism", "65"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--parallelism", "many"}));
    }

    @Test
    void rejectsInformationalAndOperationalMixtures() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--help", "--plugin", "ofgem"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--about", "--list-plugins"}));
    }

    @Test
    void reservesShortVForVerboseLogging() {
        final var arguments = processor.parse(new String[]{"--plugin", "example", "-v"});
        assertTrue(arguments.verbose());
        assertFalse(arguments.aboutRequested());
    }

    @Test
    void rejectsRemovedVersionOption() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--version"}));
    }
}
