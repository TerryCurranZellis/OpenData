/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.util.Objects;
import static java.util.Spliterators.spliteratorUnknownSize;
import java.util.stream.StreamSupport;
import javax.crypto.Cipher;

/**
 * Encrypts bootstrap passwords with an RSA public certificate and decrypts
 * them with the matching private PKCS#12 certificate store.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class RsaConfigurationPasswordCipher implements ConfigurationPasswordCipher {

    /**
     * Prefix used to mark encrypted values.
     */
    public static final String ENCRYPTED_PREFIX = "{enc}";
    public static final String KEYSTORE_PASSWORD_PROPERTY = "opendata.config.keystore.password";
    public static final String KEYSTORE_PASSWORD_ENVIRONMENT_VARIABLE = "OPENDATA_CONFIG_KEYSTORE_PASSWORD";
    public static final String DEFAULT_KEYSTORE_PASSWORD = "nopassword";

    private static final String TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final char[] EMPTY_PASSWORD = new char[0];
    private static final String PUBLIC_CERTIFICATE_RESOURCE =
            "config/security/opendata-config-public.cer";
    private static final String PRIVATE_KEY_STORE_RESOURCE =
            "config/security/opendata-config-private.pfx";

    private final Path certificatePath;
    private final Path privateKeyStorePath;
    private final char[] privateKeyStorePassword;

    /**
     * Creates the cipher using the default repository-local certificate files.
     */
    public RsaConfigurationPasswordCipher() {
        this(
                Path.of(System.getProperty("user.dir"), "src", "main", "resources", "config",
                        "security", "opendata-config-public.cer"),
                Path.of(System.getProperty("user.dir"), "src", "main", "resources", "config",
                        "security", "opendata-config-private.pfx"),
                resolvePrivateKeyStorePassword());
    }

    /**
     * Creates the cipher with explicit certificate paths.
     *
     * @param certificatePath public certificate file path
     * @param privateKeyStorePath PKCS#12 private key store file path
     */
    public RsaConfigurationPasswordCipher(
            final Path certificatePath,
            final Path privateKeyStorePath) {
        this(certificatePath, privateKeyStorePath, resolvePrivateKeyStorePassword());
    }

    /**
     * Creates the cipher with explicit certificate paths and PKCS#12 password.
     *
     * @param certificatePath public certificate file path
     * @param privateKeyStorePath PKCS#12 private key store file path
     * @param privateKeyStorePassword PKCS#12 password, or {@code null} when none is used
     */
    public RsaConfigurationPasswordCipher(
            final Path certificatePath,
            final Path privateKeyStorePath,
            final char[] privateKeyStorePassword) {
        this.certificatePath = Objects.requireNonNull(certificatePath, "certificatePath");
        this.privateKeyStorePath = Objects.requireNonNull(privateKeyStorePath, "privateKeyStorePath");
        this.privateKeyStorePassword = privateKeyStorePassword == null
                ? null
                : privateKeyStorePassword.clone();
    }

    /**
     * @inheritdoc
     * @param plainText
     * @return 
     */
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
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, readPublicKey());
            final var encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return ENCRYPTED_PREFIX + java.util.Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException | IOException exception) {
            throw new OpenDataConfigurationException("Unable to encrypt database password.", exception);
        }
    }

    /**
     * @inheritdoc
     * @param storedValue
     * @return 
     */
    @Override
    public String decrypt(final String storedValue) {
        Objects.requireNonNull(storedValue, "storedValue");
        if (storedValue.isBlank() || !isEncrypted(storedValue)) {
            return storedValue;
        }
        try {
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, readPrivateKey());
            final var decoded = java.util.Base64.getDecoder()
                    .decode(storedValue.substring(ENCRYPTED_PREFIX.length()));
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IOException exception) {
            throw new OpenDataConfigurationException("Unable to decrypt database password.", exception);
        }
    }

    /**
     * @inheritdoc
     * @param storedValue
     * @return 
     */
    @Override
    public boolean isEncrypted(final String storedValue) {
        return storedValue != null && storedValue.startsWith(ENCRYPTED_PREFIX);
    }

    /**
     * Reads the public key from the configured X.509 certificate.
     *
     * @return public key
     * @throws IOException on file read failure
     * @throws GeneralSecurityException on certificate decode failure
     */
    private PublicKey readPublicKey() throws IOException, GeneralSecurityException {
        try (InputStream input = openResource(certificatePath, PUBLIC_CERTIFICATE_RESOURCE, "public certificate")) {
            final var certificateFactory = CertificateFactory.getInstance("X.509");
            return certificateFactory.generateCertificate(input).getPublicKey();
        }
    }

    /**
     * Reads the private key from the configured PKCS#12 certificate store.
     *
     * @return private key
     * @throws IOException on file read failure
     * @throws GeneralSecurityException on key-store failure
     */
    private PrivateKey readPrivateKey() throws IOException, GeneralSecurityException {
        Exception lastFailure = null;
        for (char[] candidatePassword : candidateKeyStorePasswords()) {
            try {
                return loadPrivateKey(candidatePassword);
            } catch (GeneralSecurityException | IOException exception) {
                lastFailure = exception;
            }
        }
        throw new OpenDataConfigurationException(
                "Unable to read OpenData private key store. "
                + "Set "
                + KEYSTORE_PASSWORD_PROPERTY
                + " or "
                + KEYSTORE_PASSWORD_ENVIRONMENT_VARIABLE
                + " when the PKCS#12 file is password protected.",
                lastFailure);
    }

    /**
     * Reads the private key with one candidate PKCS#12 password.
     *
     * @param keyStorePassword candidate password, or {@code null} for no password
     * @return private key
     * @throws IOException on file read failure
     * @throws GeneralSecurityException on key-store failure
     */
    private PrivateKey loadPrivateKey(final char[] keyStorePassword) throws IOException, GeneralSecurityException {
        final var keyStore = KeyStore.getInstance("PKCS12");
        try (var input = openResource(
                privateKeyStorePath, PRIVATE_KEY_STORE_RESOURCE, "private key store")) {
            keyStore.load(input, keyStorePassword);
        }
        final var alias = StreamSupport.stream(
                spliteratorUnknownSize(keyStore.aliases().asIterator(), 0),
                false)
                .findFirst()
                .orElseThrow(() -> new OpenDataConfigurationException(
                        "Private key store does not contain a certificate alias: " + privateKeyStorePath));
        final var key = keyStore.getKey(alias, keyStorePassword);
        if (key instanceof PrivateKey privateKey) {
            return privateKey;
        }
        throw new OpenDataConfigurationException(
                "Private key store does not contain an RSA private key: " + privateKeyStorePath);
    }

    /**
     * Returns candidate PKCS#12 passwords in priority order.
     *
     * @return candidate passwords
     */
    private List<char[]> candidateKeyStorePasswords() {
        final List<char[]> candidates = new ArrayList<>();
        addCandidatePassword(candidates, privateKeyStorePassword);
        addCandidatePassword(candidates, DEFAULT_KEYSTORE_PASSWORD.toCharArray());
        addCandidatePassword(candidates, null);
        addCandidatePassword(candidates, EMPTY_PASSWORD);
        return candidates;
    }

    /**
     * Adds one unique candidate password.
     *
     * @param candidates candidate list
     * @param candidate candidate password
     */
    private static void addCandidatePassword(final List<char[]> candidates, final char[] candidate) {
        final var exists = candidates.stream().anyMatch(existing -> samePassword(existing, candidate));
        if (!exists) {
            candidates.add(candidate == null ? null : candidate.clone());
        }
    }

    /**
     * Compares two password arrays.
     *
     * @param left first password
     * @param right second password
     * @return true when both values match
     */
    private static boolean samePassword(final char[] left, final char[] right) {
        return left == right || left != null && right != null && Arrays.equals(left, right);
    }

    /**
     * Resolves the PKCS#12 password from the JVM property first, then the environment.
     *
     * @return resolved password, or {@code null} when none is configured
     */
    private static char[] resolvePrivateKeyStorePassword() {
        final var propertyValue = System.getProperty(KEYSTORE_PASSWORD_PROPERTY);
        if (propertyValue != null) {
            return propertyValue.toCharArray();
        }
        final var environmentValue = System.getenv(KEYSTORE_PASSWORD_ENVIRONMENT_VARIABLE);
        return environmentValue == null || environmentValue.isBlank()
                ? DEFAULT_KEYSTORE_PASSWORD.toCharArray()
                : environmentValue.toCharArray();
    }

    /**
     * Opens a certificate resource from the source tree when available, or
     * from the application classpath when running from a packaged JAR.
     *
     * @param path source-tree path
     * @param classpathResource packaged resource name
     * @param description human-readable description
     * @return open input stream
     * @throws IOException when the resource cannot be opened
     */
    private static InputStream openResource(
            final Path path,
            final String classpathResource,
            final String description) throws IOException {
        if (Files.isRegularFile(path)) {
            return Files.newInputStream(path);
        }
        final var input = RsaConfigurationPasswordCipher.class
                .getClassLoader()
                .getResourceAsStream(classpathResource);
        if (input != null) {
            return input;
        }
        throw new OpenDataConfigurationException(
                "OpenData configuration " + description + " was not found at "
                + path.toAbsolutePath() + " or on the classpath as " + classpathResource + '.');
    }
}
