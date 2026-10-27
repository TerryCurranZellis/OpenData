/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES-256-GCM encryption for bootstrap and database-stored passwords.
 *
 * <p>The 256-bit secret key is kept in a separate, access-restricted file,
 * outside the source tree and packaged JAR. The file contains a single
 * Base64-encoded 32-byte random key. Each encrypted value uses a fresh
 * 12-byte random nonce and a 128-bit authentication tag.</p>
 *
 * <p>Legacy RSA values ({@code {enc}}) deliberately require migration;
 * they are never mistaken for plain text.</p>
 */
public final class AesConfigurationPasswordCipher implements ConfigurationPasswordCipher {

    public static final String ENCRYPTED_PREFIX = "{aes}";
    public static final String KEY_FILE_PROPERTY = "opendata.config.key-file";
    public static final String KEY_FILE_ENVIRONMENT_VARIABLE = "OPENDATA_CONFIG_KEY_FILE";

    private static final int NONCE_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;
    private static final int KEY_LENGTH_BYTES = 32;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Path keyFile;

    /** Uses a key-file path supplied in a JVM property or environment variable. */
    public AesConfigurationPasswordCipher() {
        this(resolveKeyFile());
    }

    /** Creates a cipher pointing to an external key file (useful for testing). */
    public AesConfigurationPasswordCipher(final Path keyFile) {
        this.keyFile = keyFile;
    }

    @Override
    public String encrypt(final String plainText) {
        Objects.requireNonNull(plainText, "plainText");
        rejectLegacyRsaValue(plainText);
        if (plainText.isBlank() || isEncrypted(plainText)) {
            return plainText;
        }
        try {
            final byte[] nonce = new byte[NONCE_LENGTH];
            RANDOM.nextBytes(nonce);
            final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, loadKey(),
                    new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
            final byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            final byte[] combined = new byte[nonce.length + encrypted.length];
            System.arraycopy(nonce, 0, combined, 0, nonce.length);
            System.arraycopy(encrypted, 0, combined, nonce.length, encrypted.length);
            return ENCRYPTED_PREFIX + Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(combined);
        } catch (GeneralSecurityException exception) {
            throw new OpenDataConfigurationException("Unable to encrypt database password.", exception);
        }
    }

    @Override
    public String decrypt(final String storedValue) {
        Objects.requireNonNull(storedValue, "storedValue");
        rejectLegacyRsaValue(storedValue);
        if (!isEncrypted(storedValue)) {
            return storedValue; // Allows initial migration from local plaintext.
        }
        try {
            final byte[] combined = Base64.getUrlDecoder()
                    .decode(storedValue.substring(ENCRYPTED_PREFIX.length()));
            if (combined.length < NONCE_LENGTH + TAG_LENGTH_BITS / 8) {
                throw new OpenDataConfigurationException("Encrypted database password is truncated.");
            }
            final byte[] nonce = Arrays.copyOfRange(combined, 0, NONCE_LENGTH);
            final byte[] encrypted = Arrays.copyOfRange(combined, NONCE_LENGTH, combined.length);
            final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, loadKey(),
                    new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new OpenDataConfigurationException("Invalid Base64 in encrypted database password.", exception);
        } catch (GeneralSecurityException exception) {
            throw new OpenDataConfigurationException(
                    "Unable to decrypt database password (wrong key or changed data).", exception);
        }
    }

    @Override
    public boolean isEncrypted(final String storedValue) {
        return storedValue != null && storedValue.startsWith(ENCRYPTED_PREFIX);
    }

    private SecretKeySpec loadKey() {
        if (keyFile == null) {
            throw new OpenDataConfigurationException("AES key file is not configured. Set "
                    + KEY_FILE_ENVIRONMENT_VARIABLE + " or -D" + KEY_FILE_PROPERTY + ".");
        }
        try {
            final byte[] key = Base64.getDecoder().decode(
                    Files.readString(keyFile, StandardCharsets.US_ASCII).trim());
            if (key.length != KEY_LENGTH_BYTES) {
                throw new OpenDataConfigurationException(
                        "AES key file must contain exactly 32 random bytes encoded as Base64.");
            }
            try {
                return new SecretKeySpec(key, "AES");
            } finally {
                Arrays.fill(key, (byte) 0);
            }
        } catch (IOException | IllegalArgumentException exception) {
            throw new OpenDataConfigurationException(
                    "Unable to read valid AES key file: " + keyFile.toAbsolutePath(), exception);
        }
    }

    private static Path resolveKeyFile() {
        final String property = System.getProperty(KEY_FILE_PROPERTY);
        if (property != null && !property.isBlank()) {
            return Path.of(property);
        }
        final String environment = System.getenv(KEY_FILE_ENVIRONMENT_VARIABLE);
        return environment == null || environment.isBlank() ? null : Path.of(environment);
    }

    private static void rejectLegacyRsaValue(final String value) {
        if (value.startsWith("{enc}")) {
            throw new OpenDataConfigurationException(
                    "Legacy RSA password found. Restore the cleartext SQL password "
                    + "and re-register configuration using the AES key file.");
        }
    }
}
