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
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import javax.crypto.Cipher;

/**
 * Encrypts bootstrap passwords with an RSA public certificate and decrypts
 * them with the matching private PKCS#12 certificate store.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class RsaConfigurationPasswordCipher implements ConfigurationPasswordCipher {

    /** Prefix used to mark encrypted values. */
    public static final String ENCRYPTED_PREFIX = "{enc}";

    /** JVM property containing the PKCS#12 password. */
    public static final String KEYSTORE_PASSWORD_PROPERTY =
            "opendata.config.keystore.password";

    /** Environment variable containing the PKCS#12 password. */
    public static final String KEYSTORE_PASSWORD_ENVIRONMENT_VARIABLE =
            "OPENDATA_CONFIG_KEYSTORE_PASSWORD";

    /** Password used when no property or environment variable is supplied. */
    public static final String DEFAULT_KEYSTORE_PASSWORD = "nopassword";

    private static final String TRANSFORMATION =
            "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final char[] EMPTY_PASSWORD = new char[0];
    private static final String PUBLIC_CERTIFICATE_RESOURCE =
            "config/security/opendata-config-public.cer";
    private static final String PRIVATE_KEY_STORE_RESOURCE =
            "config/security/opendata-config-private.pfx";

    private final Path certificatePath;
    private final Path privateKeyStorePath;
    private final char[] privateKeyStorePassword;

    /** Creates the cipher using the default repository-local certificate files. */
    public RsaConfigurationPasswordCipher() {
        this(
                Path.of(System.getProperty("user.dir"), "src", "main", "resources",
                        "config", "security", "opendata-config-public.cer"),
                Path.of(System.getProperty("user.dir"), "src", "main", "resources",
                        "config", "security", "opendata-config-private.pfx"),
                resolvePrivateKeyStorePassword());
    }

    /**
     * Creates the cipher with explicit certificate paths.
     *
     * @param certificatePath public certificate file path
     * @param privateKeyStorePath PKCS#12 private key-store file path
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
     * @param privateKeyStorePath PKCS#12 private key-store file path
     * @param privateKeyStorePassword PKCS#12 password, or {@code null} when none is used
     */
    public RsaConfigurationPasswordCipher(
            final Path certificatePath,
            final Path privateKeyStorePath,
            final char[] privateKeyStorePassword) {
        this.certificatePath = Objects.requireNonNull(certificatePath, "certificatePath");
        this.privateKeyStorePath = Objects.requireNonNull(
                privateKeyStorePath, "privateKeyStorePath");
        this.privateKeyStorePassword = privateKeyStorePassword == null
                ? null
                : privateKeyStorePassword.clone();
    }

    /** {@inheritDoc} */
    @Override
    public String encrypt(final String plainText) {
        Objects.requireNonNull(plainText, "plainText");
        if (plainText.isBlank() || isEncrypted(plainText)) {
            return plainText;
        }

        try {
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, readPublicKey());
            final var encrypted = cipher.doFinal(
                    plainText.getBytes(StandardCharsets.UTF_8));
            return ENCRYPTED_PREFIX
                    + java.util.Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException | IOException exception) {
            throw new OpenDataConfigurationException(
                    "Unable to encrypt database password.", exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String decrypt(final String storedValue) {
        Objects.requireNonNull(storedValue, "storedValue");
        if (storedValue.isBlank() || !isEncrypted(storedValue)) {
            return storedValue;
        }

        try {
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, readPrivateKey());
            final var decoded = java.util.Base64.getDecoder().decode(
                    storedValue.substring(ENCRYPTED_PREFIX.length()));
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new OpenDataConfigurationException(
                    "The encrypted database password is not valid Base64 data.", exception);
        } catch (GeneralSecurityException | IOException exception) {
            throw new OpenDataConfigurationException(
                    "Unable to decrypt database password. The encrypted value may have "
                    + "been produced with a different certificate.",
                    exception);
        }
    }

    /** {@inheritDoc} */
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
        try (InputStream input = openResource(
                certificatePath, PUBLIC_CERTIFICATE_RESOURCE, "public certificate")) {
            final var certificateFactory = CertificateFactory.getInstance("X.509");
            final var publicKey = certificateFactory.generateCertificate(input).getPublicKey();
            if (!(publicKey instanceof RSAPublicKey)) {
                throw new OpenDataConfigurationException(
                        "OpenData public certificate does not contain an RSA public key: "
                        + certificatePath.toAbsolutePath());
            }
            return publicKey;
        }
    }

    /**
     * Reads the matching private key from the configured PKCS#12 store.
     *
     * @return matching private key
     * @throws IOException on file read failure
     * @throws GeneralSecurityException on key-store failure
     */
    private PrivateKey readPrivateKey() throws IOException, GeneralSecurityException {
        final PublicKey expectedPublicKey = readPublicKey();
        Exception lastFailure = null;

        for (char[] candidatePassword : candidateKeyStorePasswords()) {
            try {
                return loadMatchingPrivateKey(candidatePassword, expectedPublicKey);
            } catch (GeneralSecurityException | IOException exception) {
                lastFailure = exception;
            }
        }

        throw new OpenDataConfigurationException(
                "Unable to open the OpenData PKCS#12 private-key store. Verify that "
                + KEYSTORE_PASSWORD_PROPERTY + " or "
                + KEYSTORE_PASSWORD_ENVIRONMENT_VARIABLE
                + " contains the password used when the .pfx file was exported. "
                + "The built-in fallback password is '" + DEFAULT_KEYSTORE_PASSWORD + "'.",
                lastFailure);
    }

    /**
     * Loads the PKCS#12 store and returns the RSA private key whose public key
     * matches the separately configured public certificate.
     *
     * @param keyStorePassword candidate store/key password
     * @param expectedPublicKey public key read from the .cer file
     * @return matching private key
     * @throws IOException when the store cannot be read or decrypted
     * @throws GeneralSecurityException on key-store access failure
     */
    private PrivateKey loadMatchingPrivateKey(
            final char[] keyStorePassword,
            final PublicKey expectedPublicKey)
            throws IOException, GeneralSecurityException {

        final var keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream input = openResource(
                privateKeyStorePath,
                PRIVATE_KEY_STORE_RESOURCE,
                "private key store")) {
            keyStore.load(input, keyStorePassword);
        }

        final Enumeration<String> aliases = keyStore.aliases();
        boolean privateKeyFound = false;

        while (aliases.hasMoreElements()) {
            final String alias = aliases.nextElement();
            if (!keyStore.isKeyEntry(alias)) {
                continue;
            }

            final Key key = keyStore.getKey(alias, keyStorePassword);
            if (!(key instanceof PrivateKey privateKey)) {
                continue;
            }

            privateKeyFound = true;
            if (isMatchingRsaKeyPair(expectedPublicKey, privateKey)) {
                return privateKey;
            }
        }

        if (privateKeyFound) {
            throw new OpenDataConfigurationException(
                    "The public .cer certificate and private .pfx store do not contain "
                    + "the same RSA key pair. Re-export both files from the same certificate.");
        }

        throw new OpenDataConfigurationException(
                "The PKCS#12 store does not contain a private-key entry: "
                + privateKeyStorePath.toAbsolutePath());
    }

    /**
     * Determines whether an RSA public and private key have the same modulus.
     *
     * @param publicKey public key
     * @param privateKey private key
     * @return {@code true} when the keys form one RSA key pair
     */
    private static boolean isMatchingRsaKeyPair(
            final PublicKey publicKey,
            final PrivateKey privateKey) {
        return publicKey instanceof RSAPublicKey rsaPublicKey
                && privateKey instanceof RSAPrivateKey rsaPrivateKey
                && rsaPublicKey.getModulus().equals(rsaPrivateKey.getModulus());
    }

    /** Returns candidate PKCS#12 passwords in priority order. */
    private List<char[]> candidateKeyStorePasswords() {
        final List<char[]> candidates = new ArrayList<>();
        addCandidatePassword(candidates, privateKeyStorePassword);
        addCandidatePassword(candidates, DEFAULT_KEYSTORE_PASSWORD.toCharArray());
        addCandidatePassword(candidates, null);
        addCandidatePassword(candidates, EMPTY_PASSWORD);
        return candidates;
    }

    /** Adds one unique candidate password. */
    private static void addCandidatePassword(
            final List<char[]> candidates,
            final char[] candidate) {
        final boolean exists = candidates.stream()
                .anyMatch(existing -> samePassword(existing, candidate));
        if (!exists) {
            candidates.add(candidate == null ? null : candidate.clone());
        }
    }

    /** Compares two password arrays. */
    private static boolean samePassword(final char[] left, final char[] right) {
        return left == right
                || left != null && right != null && Arrays.equals(left, right);
    }

    /**
     * Resolves the PKCS#12 password from the JVM property first, then the
     * environment variable, and finally the built-in default.
     */
    private static char[] resolvePrivateKeyStorePassword() {
        final String propertyValue = System.getProperty(KEYSTORE_PASSWORD_PROPERTY);
        if (propertyValue != null) {
            return propertyValue.toCharArray();
        }

        final String environmentValue = System.getenv(
                KEYSTORE_PASSWORD_ENVIRONMENT_VARIABLE);
        return environmentValue == null || environmentValue.isBlank()
                ? DEFAULT_KEYSTORE_PASSWORD.toCharArray()
                : environmentValue.toCharArray();
    }

    /**
     * Opens a certificate resource from the source tree when available, or
     * from the application classpath when running from a packaged JAR.
     */
    private static InputStream openResource(
            final Path path,
            final String classpathResource,
            final String description) throws IOException {
        if (Files.isRegularFile(path)) {
            return Files.newInputStream(path);
        }

        final InputStream input = RsaConfigurationPasswordCipher.class
                .getClassLoader()
                .getResourceAsStream(classpathResource);
        if (input != null) {
            return input;
        }

        throw new OpenDataConfigurationException(
                "OpenData configuration " + description + " was not found at "
                + path.toAbsolutePath() + " or on the classpath as "
                + classpathResource + '.');
    }
}
