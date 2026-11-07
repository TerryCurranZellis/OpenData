/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.setup;

import com.towermarsh.opendata.config.PluginRegistration;
import com.towermarsh.opendata.plugin.PluginDescriptor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies forced-disabled plugin registration and AES key-file lifecycle.
 *
 * @author Terry Curran
 * @version 4.0.0
 */
class InitialisationServiceTest {

    @TempDir
    Path directory;

    /** Ensures a plugin definition enabled in source registers as disabled. */
    @Test
    void forcesAllNewPluginsToDisabledWithoutMutatingOriginal() {
        final var source = new PluginRegistration(
                new PluginDescriptor("ofgem", "Ofgem", "", "example.Ofgem", true, 1),
                Map.of("plugin.id", "ofgem", "plugin.enabled", "true"));
        final var result = InitialisationService.asDisabled(source);
        assertFalse(result.descriptor().enabled());
        assertEquals("false", result.properties().get("plugin.enabled"));
        assertTrue(source.descriptor().enabled());
        assertEquals("true", source.properties().get("plugin.enabled"));
    }

    /** Ensures key creation is random, well formed, and never overwrites an existing key. */
    @Test
    void generatesAndPreservesAesKey() throws Exception {
        final var key = directory.resolve("keys/setup.key");
        InitialisationService.prepareKey(key);
        final var first = Files.readString(key, StandardCharsets.US_ASCII);
        assertEquals(32, Base64.getDecoder().decode(first.strip()).length);
        InitialisationService.prepareKey(key);
        assertEquals(first, Files.readString(key, StandardCharsets.US_ASCII));
    }
}
