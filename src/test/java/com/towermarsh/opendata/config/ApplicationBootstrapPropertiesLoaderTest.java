/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests bootstrap property loading and encrypted password persistence.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
class ApplicationBootstrapPropertiesLoaderTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void storesEncryptedPasswordAndLoadsPlainTextValue() throws Exception {
        final ConfigurationPasswordCipher cipher = new ConfigurationPasswordCipher() {
            @Override
            public String encrypt(final String plainText) {
                return plainText.isBlank()
                        ? plainText
                        : "{enc}" + Base64.getEncoder().encodeToString(plainText.getBytes());
            }

            @Override
            public String decrypt(final String storedValue) {
                return storedValue.startsWith("{enc}")
                        ? new String(Base64.getDecoder().decode(storedValue.substring(5)))
                        : storedValue;
            }

            @Override
            public boolean isEncrypted(final String storedValue) {
                return storedValue != null && storedValue.startsWith("{enc}");
            }
        };
        final var propertiesFile = temporaryDirectory.resolve("config/application.properties");
        final var loader = new ApplicationBootstrapPropertiesLoader(
                propertiesFile,
                Thread.currentThread().getContextClassLoader(),
                cipher);
        final var bootstrap = new ApplicationBootstrapProperties(
                "2.0.0",
                "jdbc:sqlserver://localhost;databaseName=OpenData",
                "OpenData",
                "secret-value",
                true);

        loader.store(bootstrap);

        final var storedText = Files.readString(propertiesFile);
        assertFalse(storedText.contains("secret-value"));
        assertTrue(storedText.contains("{enc}"));

        final var loaded = loader.load(Map.of());
        assertTrue(loaded.useDatabaseProperties());
        org.junit.jupiter.api.Assertions.assertEquals("secret-value", loaded.databasePassword());
    }
}
