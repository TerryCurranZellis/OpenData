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
            "--plugin", "openmeteo,ofgem", "--plugin", "octopus",
            "--execute", "--parallelism", "3"
        });
        assertEquals(List.of("openmeteo", "ofgem", "octopus"), arguments.pluginIds());
        assertEquals(3, arguments.parallelism().orElseThrow());
        assertEquals(PluginCommand.RUN, arguments.command());
        assertTrue(arguments.executeRequested());
    }

    @Test
    void recognisesAllForRun() {
        final var arguments = processor.parse(new String[]{"--plugin", "all", "--execute"});
        assertTrue(arguments.allPluginsRequested());
        assertTrue(arguments.pluginIds().isEmpty());
        assertTrue(arguments.executeRequested());
        assertTrue(arguments.runRequested());
    }

    @Test
    void acceptsExecuteShortOption() {
        final var arguments = processor.parse(new String[]{"-p", "ofgem", "-x"});
        assertTrue(arguments.executeRequested());
        assertTrue(arguments.runRequested());
    }

    @Test
    void acceptsDetailForExactlyOneNamedPlugin() {
        final var arguments = processor.parse(new String[]{"--plugin", "ofgem", "--detail"});
        assertEquals(List.of("ofgem"), arguments.pluginIds());
        assertEquals(PluginCommand.DETAIL, arguments.command());
        assertTrue(arguments.detailRequested());
        assertFalse(arguments.executeRequested());
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void rejectsDetailForAllOrMultiplePlugins() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "all", "--detail"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--plugin", "octopus", "--detail"
                }));
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void rejectsExecuteAndRunOptionsWithDetail() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "ofgem", "--detail", "--execute"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "ofgem", "--detail", "--dry-run"}));
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void requiresExecuteForPluginRunsAndDryRuns() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "ofgem"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "ofgem", "--dry-run"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "ofgem", "--parallelism", "2"}));
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void rejectsExecuteWithAdministrationOperations() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "ofgem", "--register", "--execute"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "ofgem", "--disable", "-x"}));
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

        final var dryRun = processor.parse(new String[]{"--plugin", "ofgem", "-x", "-n"});
        assertEquals(PluginCommand.RUN, dryRun.command());
        assertTrue(dryRun.executeRequested());
        assertTrue(dryRun.dryRun());
    }

    @Test
    void acceptsLongDryRun() {
        final var arguments = processor.parse(new String[]{"--plugin all --execute --dry-run"});
        assertTrue(arguments.allPluginsRequested());
        assertTrue(arguments.executeRequested());
        assertTrue(arguments.dryRun());
    }

    @Test
    void acceptsInformationalOptionsWithoutPluginSelection() {
        assertTrue(processor.parse(new String[]{"--help"}).helpRequested());
        assertTrue(processor.parse(new String[]{"-a"}).aboutRequested());
        assertTrue(processor.parse(new String[]{"-l"}).listPluginsRequested());
    }

    @Test
    void acceptsGuiOptionsWithoutPluginSelection() {
        final var longOption = processor.parse(new String[]{"--gui"});
        assertTrue(longOption.guiRequested());
        assertEquals(PluginCommand.GUI, longOption.command());

        final var shortOption = processor.parse(new String[]{"-g"});
        assertTrue(shortOption.guiRequested());
        assertEquals(PluginCommand.GUI, shortOption.command());
    }

    @Test
    void defaultsEmptyCommandLinesToGui() {
        assertTrue(processor.parse(new String[0]).guiRequested());
        assertTrue(processor.parse(new String[]{""}).guiRequested());
        assertTrue(processor.parse(new String[]{"   "}).guiRequested());
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
    @SuppressWarnings("ThrowableResultIgnored")
    void rejectsUnterminatedQuotedLauncherArgument() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin example --register --file \"C:\\OpenData Files"}));
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
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
                () -> processor.parse(new String[]{"--detail"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--execute"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--dry-run"}));
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void rejectsMutuallyExclusiveAdministrationOperations() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--register", "--unregister"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--enable", "--disable"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--detail", "--disable"}));
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void rejectsDryRunWithAdministration() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--register", "--dry-run"}));
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void rejectsInvalidFileCombinations() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--execute", "--file", "ofgem.properties"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "all", "--register", "--file", "plugins.properties"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--plugin", "octopus", "--register",
                    "--file", "plugins.properties"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--detail", "--file", "ofgem.properties"}));
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void rejectsDuplicateAndAllCombinedPluginSelections() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "openmeteo", "--plugin", "openmeteo", "--execute"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--plugin", "all,openmeteo", "--execute"}));
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void validatesParallelismRangeAndType() {
        assertEquals(1, processor.parse(new String[]{
            "--plugin", "ofgem", "--execute", "--parallelism", "1"}).parallelism().orElseThrow());
        assertEquals(64, processor.parse(new String[]{
            "--plugin", "ofgem", "--execute", "--parallelism", "64"}).parallelism().orElseThrow());
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--execute", "--parallelism", "0"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--execute", "--parallelism", "65"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{
                    "--plugin", "ofgem", "--execute", "--parallelism", "many"}));
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void rejectsInformationalAndOperationalMixtures() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--help", "--plugin", "ofgem"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--about", "--list-plugins"}));
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--help", "--execute"}));
    }

    @Test
    void reservesShortVForVerboseLogging() {
        final var arguments = processor.parse(new String[]{"--plugin", "example", "--execute", "-v"});
        assertTrue(arguments.verbose());
        assertFalse(arguments.aboutRequested());
    }

    @Test
    @SuppressWarnings("ThrowableResultIgnored")
    void rejectsRemovedVersionOption() {
        assertThrows(CommandLineProcessingException.class,
                () -> processor.parse(new String[]{"--version"}));
    }
}
