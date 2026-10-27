/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests for short, authenticated AES-256-GCM password values. */
class AesConfigurationPasswordCipherTest {

    @TempDir Path temporaryDirectory;

    private AesConfigurationPasswordCipher newCipher(final String fileName) throws Exception {
        final byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        final Path file = temporaryDirectory.resolve(fileName);
        Files.writeString(file, Base64.getEncoder().encodeToString(key), StandardCharsets.US_ASCII);
        return new AesConfigurationPasswordCipher(file);
    }

    @Test
    void roundTripsWithShortCiphertext() throws Exception {
        final var cipher = newCipher("key-a.txt");
        final String encrypted = cipher.encrypt("SQL-Password12");
        assertTrue(encrypted.startsWith("{aes}"));
        assertTrue(encrypted.length() < 80);
        assertEquals("SQL-Password12", cipher.decrypt(encrypted));
        assertEquals(encrypted, cipher.encrypt(encrypted));
    }

    @Test
    void usesFreshNonceForEveryEncryption() throws Exception {
        final var cipher = newCipher("key-a.txt");
        assertNotEquals(cipher.encrypt("same password"), cipher.encrypt("same password"));
    }

    @Test
    void rejectsWrongKeyAndModifiedCiphertext() throws Exception {
        final var first = newCipher("key-a.txt");
        final var second = newCipher("key-b.txt");
        final String encrypted = first.encrypt("secret");
        assertThrows(OpenDataConfigurationException.class,
                () -> second.decrypt(encrypted));
        // Change a nonce byte (rather than unused low bits of the final Base64 char).
        final int position = AesConfigurationPasswordCipher.ENCRYPTED_PREFIX.length() + 2;
        final char oldCharacter = encrypted.charAt(position);
        final String modified = encrypted.substring(0, position)
                + (oldCharacter == 'A' ? 'B' : 'A')
                + encrypted.substring(position + 1);
        assertThrows(OpenDataConfigurationException.class,
                () -> first.decrypt(modified));
    }

    @Test
    void supportsPlaintextDuringMigrationButRefusesLegacyRsa() throws Exception {
        final var cipher = newCipher("key-a.txt");
        assertEquals("plain", cipher.decrypt("plain"));
        assertFalse(cipher.isEncrypted("plain"));
        assertThrows(OpenDataConfigurationException.class,
                () -> cipher.decrypt("{enc}legacy"));
        assertThrows(OpenDataConfigurationException.class,
                () -> cipher.encrypt("{enc}legacy"));
    }

    @Test
    void failsWhenKeyFileIsMissing() {
        final var cipher = new AesConfigurationPasswordCipher(
                temporaryDirectory.resolve("missing.key"));
        assertThrows(OpenDataConfigurationException.class,
                () -> cipher.encrypt("secret"));
    }
}
