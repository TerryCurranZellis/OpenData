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
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.Cipher;

/**
 * Encrypts bootstrap passwords with a repository-local RSA key pair.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class RsaConfigurationPasswordCipher implements ConfigurationPasswordCipher {

    /**
     * Prefix used to mark encrypted values.
     */
    public static final String ENCRYPTED_PREFIX = "{enc}";

    private static final String TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int KEY_SIZE = 2048;

    private final Path publicKeyPath;
    private final Path privateKeyPath;

    /**
     * Creates the cipher using the default repository-local key file locations.
     */
    public RsaConfigurationPasswordCipher() {
        this(
                Path.of(System.getProperty("user.dir"), "src", "main", "resources", "config",
                        "security", "opendata-config-public.key"),
                Path.of(System.getProperty("user.dir"), "src", "main", "resources", "config",
                        "security", "opendata-config-private.key"));
    }

    /**
     * Creates the cipher with explicit key file paths.
     *
     * @param publicKeyPath public key file path
     * @param privateKeyPath private key file path
     */
    public RsaConfigurationPasswordCipher(
            final Path publicKeyPath,
            final Path privateKeyPath) {
        this.publicKeyPath = Objects.requireNonNull(publicKeyPath, "publicKeyPath");
        this.privateKeyPath = Objects.requireNonNull(privateKeyPath, "privateKeyPath");
    }

    @Override
    public String encrypt(final String plainText) {
        Objects.requireNonNull(plainText, "plainText");
        if (plainText.isBlank()) {
            return plainText;
        }
        if (isEncrypted(plainText)) {
            return plainText;
        }
        try {
            ensureKeyPair();
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, readPublicKey());
            final var encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return ENCRYPTED_PREFIX + Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException | IOException exception) {
            throw new OpenDataConfigurationException("Unable to encrypt database password.", exception);
        }
    }

    @Override
    public String decrypt(final String storedValue) {
        Objects.requireNonNull(storedValue, "storedValue");
        if (storedValue.isBlank() || !isEncrypted(storedValue)) {
            return storedValue;
        }
        try {
            ensureKeyPair();
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, readPrivateKey());
            final var decoded = Base64.getDecoder()
                    .decode(storedValue.substring(ENCRYPTED_PREFIX.length()));
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IOException exception) {
            throw new OpenDataConfigurationException("Unable to decrypt database password.", exception);
        }
    }

    @Override
    public boolean isEncrypted(final String storedValue) {
        return storedValue != null && storedValue.startsWith(ENCRYPTED_PREFIX);
    }

    /**
     * Creates the key pair when it does not already exist.
     *
     * @throws GeneralSecurityException on key generation failure
     * @throws IOException on file write failure
     */
    private void ensureKeyPair() throws GeneralSecurityException, IOException {
        if (Files.isRegularFile(publicKeyPath) && Files.isRegularFile(privateKeyPath)) {
            return;
        }
        createParentDirectories(publicKeyPath);
        createParentDirectories(privateKeyPath);
        final var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(KEY_SIZE);
        final KeyPair keyPair = generator.generateKeyPair();
        writeKey(publicKeyPath, keyPair.getPublic().getEncoded());
        writeKey(privateKeyPath, keyPair.getPrivate().getEncoded());
    }

    /**
     * Writes one binary key in Base64 form.
     *
     * @param path destination path
     * @param encoded encoded key bytes
     * @throws IOException on write failure
     */
    private static void writeKey(final Path path, final byte[] encoded) throws IOException {
        Files.writeString(path, Base64.getEncoder().encodeToString(encoded), StandardCharsets.UTF_8);
    }

    /**
     * Creates a parent directory when the path has one.
     *
     * @param path target file path
     * @throws IOException on directory creation failure
     */
    private static void createParentDirectories(final Path path) throws IOException {
        final var parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /**
     * Reads the public key.
     *
     * @return public key
     * @throws IOException on read failure
     * @throws GeneralSecurityException on key decode failure
     */
    private PublicKey readPublicKey() throws IOException, GeneralSecurityException {
        final var encoded = Base64.getDecoder().decode(Files.readString(publicKeyPath, StandardCharsets.UTF_8).trim());
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
    }

    /**
     * Reads the private key.
     *
     * @return private key
     * @throws IOException on read failure
     * @throws GeneralSecurityException on key decode failure
     */
    private PrivateKey readPrivateKey() throws IOException, GeneralSecurityException {
        final var encoded = Base64.getDecoder().decode(Files.readString(privateKeyPath, StandardCharsets.UTF_8).trim());
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encoded));
    }
}
