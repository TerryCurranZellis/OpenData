/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests discovery of GUI-registerable plugin properties files.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
class PluginConfigurationDirectoryScannerTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void findsPropertiesAndIgnoresIndexAndOtherFiles() throws IOException {
        final var first = Files.createDirectories(temporaryDirectory.resolve("config/plugins"));
        final var second = Files.createDirectories(temporaryDirectory.resolve("source/plugins"));
        Files.writeString(first.resolve("zeta.properties"), "plugin.id=zeta");
        Files.writeString(first.resolve("index.properties"), "plugins=zeta");
        Files.writeString(first.resolve("notes.txt"), "ignore");
        Files.writeString(second.resolve("alpha.properties"), "plugin.id=alpha");

        final var scanner = new PluginConfigurationDirectoryScanner(List.of(first, second));

        assertEquals(
                List.of(
                        first.resolve("zeta.properties").toAbsolutePath().normalize(),
                        second.resolve("alpha.properties").toAbsolutePath().normalize()),
                scanner.scan());
    }

    @Test
    void missingDirectoriesProduceEmptyResult() {
        final var scanner = new PluginConfigurationDirectoryScanner(
                List.of(temporaryDirectory.resolve("missing")));

        assertEquals(List.of(), scanner.scan());
    }
}
