/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests compiled Windows Help resolution without starting a JavaFX toolkit. */
class OpenDataHelpLauncherTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void recognisesWindowsNamesCaseInsensitively() {
        assertTrue(OpenDataHelpLauncher.isWindows("Windows 11"));
        assertTrue(OpenDataHelpLauncher.isWindows("WINDOWS 10"));
        assertFalse(OpenDataHelpLauncher.isWindows("Linux"));
    }

    @Test
    void resolvesPackagedHelpDirectoryBeforeGeneratedDocumentation() throws IOException {
        final var packaged = temporaryDirectory
                .resolve("help")
                .resolve(OpenDataHelpLauncher.HELP_FILE_NAME);
        final var generated = temporaryDirectory
                .resolve("docs")
                .resolve("build")
                .resolve("help")
                .resolve("TechnicalUserGuide")
                .resolve(OpenDataHelpLauncher.HELP_FILE_NAME);
        Files.createDirectories(packaged.getParent());
        Files.createDirectories(generated.getParent());
        Files.writeString(packaged, "packaged");
        Files.writeString(generated, "generated");

        assertEquals(packaged.toAbsolutePath().normalize(),
                OpenDataHelpLauncher.locateHelpFile(List.of(temporaryDirectory)).orElseThrow());
    }

    @Test
    void resolvesGeneratedHelpForSourceTreeExecution() throws IOException {
        final var generated = temporaryDirectory
                .resolve("docs")
                .resolve("build")
                .resolve("help")
                .resolve("TechnicalUserGuide")
                .resolve(OpenDataHelpLauncher.HELP_FILE_NAME);
        Files.createDirectories(generated.getParent());
        Files.writeString(generated, "generated");

        assertEquals(generated.toAbsolutePath().normalize(),
                OpenDataHelpLauncher.locateHelpFile(List.of(temporaryDirectory)).orElseThrow());
    }

    @Test
    void returnsEmptyWhenCompiledHelpIsAbsent() {
        assertTrue(OpenDataHelpLauncher.locateHelpFile(List.of(temporaryDirectory)).isEmpty());
    }
}
