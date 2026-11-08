/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.setup;

import com.towermarsh.opendata.config.AesConfigurationPasswordCipher;
import com.towermarsh.opendata.config.ApplicationBootstrapProperties;
import com.towermarsh.opendata.config.ApplicationBootstrapPropertiesLoader;
import com.towermarsh.opendata.config.OpenDataConfigurationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies first-run flag handling and encrypted write-on-success behaviour.
 *
 * @author Terry Curran
 * @version 4.0.0
 */
class FirstRunPropertiesTest {

    @TempDir
    Path directory;

    /** Ensures no database secret is needed to detect the initialisation switch. */
    @Test
    void initialisationGateDoesNotRequireDatabasePassword() throws Exception {
        final var file = directory.resolve("config/application.properties");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "init=true\ncustom.value=keep-me\n");
        final var loader = new ApplicationBootstrapPropertiesLoader(file,
                getClass().getClassLoader(), new AesConfigurationPasswordCipher(directory.resolve("key")));
        assertTrue(loader.initialisationRequested());
        assertThrows(OpenDataConfigurationException.class, () -> loader.load(Map.of()));
    }

    /** A failed setup must never change init to false or create a main-GUI-ready file. */
    @Test
    void failedDatabaseInitialisationKeepsInitTrue() throws Exception {
        final var file = directory.resolve("retry/application.properties");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "init=true\n");
        final var key = directory.resolve("retry/key.txt");
        final var loader = new ApplicationBootstrapPropertiesLoader(file,
                getClass().getClassLoader(), new AesConfigurationPasswordCipher(key));
        final var resolver = org.mockito.Mockito.mock(
                com.towermarsh.opendata.config.PluginRegistrationResolver.class);
        org.mockito.Mockito.when(resolver.resolveAllPackaged()).thenReturn(java.util.List.of());
        final var sql = org.mockito.Mockito.mock(SqlServerInitialiser.class);
        org.mockito.Mockito.doThrow(new IllegalStateException("Simulated SQL connection failure"))
                .when(sql).initialise(org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any());
        final var service = new InitialisationService(loader, resolver,
                new com.towermarsh.opendata.config.PluginConfigurationDirectoryScanner(
                        java.util.List.of(directory.resolve("nonexistent"))), sql);
        final var request = new InitialisationRequest("localhost", 1433, "OpenData",
                "admin", "not-persisted", "OpenData", "application-password", key, false);
        assertThrows(IllegalStateException.class,
                () -> service.initialise(request, ignored -> { }));
        assertTrue(loader.initialisationRequested());
        assertFalse(Files.readString(file).contains("database.password"));
    }

    /** Ensures only the last successful call clears init and writes AES ciphertext. */
    @Test
    void completingSetupPreservesOtherSettings() throws Exception {
        final var file = directory.resolve("config/application.properties");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "init=true\ncustom.value=keep-me\n");
        final var key = directory.resolve("secrets/key.txt");
        InitialisationService.prepareKey(key);
        final var cipher = new AesConfigurationPasswordCipher(key);
        final var loader = new ApplicationBootstrapPropertiesLoader(file, getClass().getClassLoader(), cipher);
        loader.completeInitialisation(new ApplicationBootstrapProperties(
                "4.0.0", "jdbc:sqlserver://localhost;databaseName=OpenData",
                "OpenData", "secret", true), key);
        assertFalse(loader.initialisationRequested());
        final var text = Files.readString(file);
        assertTrue(text.contains("custom.value=keep-me"));
        assertFalse(text.contains("database.password=secret"));
        assertTrue(text.contains("{aes}"));
        assertEquals("secret", loader.load(Map.of()).databasePassword());
    }
}
