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
 * <p>
 * The 256-bit secret key is kept in a separate, access-restricted file, outside
 * the source tree and packaged JAR. The file contains a single Base64-encoded
 * 32-byte random key. Each encrypted value uses a fresh 12-byte random nonce
 * and a 128-bit authentication tag.</p>
 *
 * <p>
 * Legacy RSA values ({@code {enc}}) deliberately require migration; they are
 * never mistaken for plain text.</p>
 *
 * @author Terry Curran
 * @version 4.0.0
 */
public class AesConfigurationPasswordCipher implements ConfigurationPasswordCipher {

    public static final String ENCRYPTED_PREFIX = "{aes}";
    public static final String KEY_FILE_SETTING = "security.aes-key-file";
    /**
     * @deprecated Since 4.0.0, use {@code security.aes-key-file} in
     * {@code config/application.properties}; JVM key location is legacy only.
     */
    @Deprecated(since = "4.0.0", forRemoval = false)
    public static final String KEY_FILE_PROPERTY = "opendata.config.key-file";
    /**
     * @deprecated Since 4.0.0, use {@code security.aes-key-file} in
     * {@code config/application.properties}; environment key location is legacy only.
     */
    @Deprecated(since = "4.0.0", forRemoval = false)
    public static final String KEY_FILE_ENVIRONMENT_VARIABLE = "OPENDATA_CONFIG_KEY_FILE";

    private static final int NONCE_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;
    private static final int KEY_LENGTH_BYTES = 32;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Path keyFile;

    /**
     * Loads the AES key path from the ordinary application properties file.
     * The previous JVM/environment fallbacks remain for older installations.
     */
    public AesConfigurationPasswordCipher() {
        this(resolveKeyFile());
    }

    /**
     * Creates a cipher pointing to an external key file (useful for testing).
     *
     * @param keyFile absolute or relative path to the Base64 AES key file
     */
    public AesConfigurationPasswordCipher(Path keyFile) {
        this.keyFile = keyFile;
    }

    /**
     * Encrypt password
     * @param plainText plain text password
     * @return encrypted password
     */
    @Override
    public String encrypt(String plainText) {
        Objects.requireNonNull(plainText, "plainText");
        rejectLegacyRsaValue(plainText);
        if (plainText.isBlank() || isEncrypted(plainText)) {
            return plainText;
        }
        try {
            var nonce = new byte[NONCE_LENGTH];
            RANDOM.nextBytes(nonce);
            var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, loadKey(),
                    new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
            var encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            var combined = new byte[nonce.length + encrypted.length];
            System.arraycopy(nonce, 0, combined, 0, nonce.length);
            System.arraycopy(encrypted, 0, combined, nonce.length, encrypted.length);
            return ENCRYPTED_PREFIX + Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(combined);
        } catch (GeneralSecurityException exception) {
            throw new OpenDataConfigurationException("Unable to encrypt database password.", exception);
        }
    }

    /**
     * decrypt password
     * @param storedValue encrypted or plain stored value
     * @return decrypted plain-text password
     */
    @Override
    public String decrypt(final String storedValue) {
        Objects.requireNonNull(storedValue, "storedValue");
        rejectLegacyRsaValue(storedValue);
        if (!isEncrypted(storedValue)) {
            return storedValue; // Allows initial migration from local plaintext.
        }
        try {
            var combined = Base64.getUrlDecoder()
                    .decode(storedValue.substring(ENCRYPTED_PREFIX.length()));
            if (combined.length < NONCE_LENGTH + TAG_LENGTH_BITS / 8) {
                throw new OpenDataConfigurationException("Encrypted database password is truncated.");
            }
            var nonce = Arrays.copyOfRange(combined, 0, NONCE_LENGTH);
            var encrypted = Arrays.copyOfRange(combined, NONCE_LENGTH, combined.length);
            var cipher = Cipher.getInstance("AES/GCM/NoPadding");
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

    /**
     * checks if a password is encrypted
     * @param storedValue encrypted or plain stored value
     * @return decrypted plain-text password
     */
    @Override
    public boolean isEncrypted(final String storedValue) {
        return storedValue != null && storedValue.startsWith(ENCRYPTED_PREFIX);
    }

    /**
     * Reads the configured 256-bit key and returns a JCE AES key.
     *
     * @return key specification
     */
    private SecretKeySpec loadKey() {
        if (keyFile == null) {
            throw new OpenDataConfigurationException("AES key file is not configured. Set " + KEY_FILE_SETTING
                    + " in config/application.properties.");
        }
        try {
            var key = Base64.getDecoder().decode(
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

    /**
     * Resolves the key location through the existing file-based configuration.
     * Deprecated JVM/environment settings are supported only as fallbacks.
     *
     * @return configured key file or null
     */
    private static Path resolveKeyFile() {
        var configured = keyFileFromProperties(ApplicationBootstrapPropertiesLoader.DEFAULT_FILE);
        if (configured == null) {
            configured = keyFileFromClasspath();
        }
        if (configured != null) {
            return configured;
        }
        var legacy = System.getProperty(KEY_FILE_PROPERTY);
        if (legacy == null || legacy.isBlank()) {
            legacy = System.getenv(KEY_FILE_ENVIRONMENT_VARIABLE);
        }
        return legacy == null || legacy.isBlank() ? null : Path.of(legacy);
    }

    /**
     * Reads an external file without requiring password decryption.
     *
     * @param file properties file
     * @return key path or null
     */
    private static Path keyFileFromProperties(final Path file) {
        if (!Files.isRegularFile(file)) {
            return null;
        }
        try (var input = Files.newInputStream(file)) {
            final var values = new java.util.Properties();
            values.load(new java.io.InputStreamReader(input, StandardCharsets.UTF_8));
            final var value = values.getProperty(KEY_FILE_SETTING, "").trim();
            return value.isBlank() ? null : Path.of(value);
        } catch (IOException exception) {
            throw new OpenDataConfigurationException("Unable to read AES key path from " + file, exception);
        }
    }

    /**
     * Reads the packaged key setting only for legacy deployments.
     *
     * @return key path or null
     */
    private static Path keyFileFromClasspath() {
        final var loader = Thread.currentThread().getContextClassLoader();
        try (var input = loader.getResourceAsStream("config/application.properties")) {
            if (input == null) {
                return null;
            }
            final var values = new java.util.Properties();
            values.load(new java.io.InputStreamReader(input, StandardCharsets.UTF_8));
            final var value = values.getProperty(KEY_FILE_SETTING, "").trim();
            return value.isBlank() ? null : Path.of(value);
        } catch (IOException exception) {
            throw new OpenDataConfigurationException("Unable to read packaged AES key setting.", exception);
        }
    }

    /**
     * Refuses to misinterpret old RSA ciphertext as an unencrypted password.
     *
     * @param value stored secret
     */
    private static void rejectLegacyRsaValue(String value) {
        if (value.startsWith("{enc}")) {
            throw new OpenDataConfigurationException(
                    "Legacy RSA password found. Restore the cleartext SQL password "
                    + "and re-register configuration using the AES key file.");
        }
    }
}
