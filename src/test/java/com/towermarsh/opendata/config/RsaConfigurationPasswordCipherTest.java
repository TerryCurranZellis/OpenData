/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests RSA-backed configuration password encryption.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
class RsaConfigurationPasswordCipherTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void decryptsWithConfiguredPrivateKeyStorePassword() throws Exception {
        final var certificatePath = temporaryDirectory.resolve("opendata-config-public.cer");
        final var privateKeyStorePath = temporaryDirectory.resolve("opendata-config-private.pfx");
        createCertificatePair(privateKeyStorePath, certificatePath, "changeit");

        final var encryptingCipher = new RsaConfigurationPasswordCipher(certificatePath, privateKeyStorePath);
        final var encrypted = encryptingCipher.encrypt("secret-value");

        final var decryptingCipher = new RsaConfigurationPasswordCipher(
                certificatePath,
                privateKeyStorePath,
                "changeit".toCharArray());

        assertTrue(encrypted.startsWith(RsaConfigurationPasswordCipher.ENCRYPTED_PREFIX));
        assertEquals("secret-value", decryptingCipher.decrypt(encrypted));
    }


    @Test
    void decryptsWithDefaultOpenDataPrivateKeyStorePassword() throws Exception {
        final var certificatePath = temporaryDirectory.resolve("opendata-config-public.cer");
        final var privateKeyStorePath = temporaryDirectory.resolve("opendata-config-private.pfx");
        createCertificatePair(privateKeyStorePath, certificatePath, "nopassword");

        final var cipher = new RsaConfigurationPasswordCipher(certificatePath, privateKeyStorePath);
        final var encrypted = cipher.encrypt("database-secret");

        assertEquals("database-secret", cipher.decrypt(encrypted));
    }

    private static void createCertificatePair(
            final Path privateKeyStorePath,
            final Path certificatePath,
            final String password) throws IOException, InterruptedException {
        runKeyTool(List.of(
                "keytool",
                "-genkeypair",
                "-alias", "opendata",
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-storetype", "PKCS12",
                "-keystore", privateKeyStorePath.toString(),
                "-storepass", password,
                "-keypass", password,
                "-dname", "CN=OpenData Test",
                "-validity", "365",
                "-noprompt"));
        runKeyTool(List.of(
                "keytool",
                "-exportcert",
                "-alias", "opendata",
                "-keystore", privateKeyStorePath.toString(),
                "-storepass", password,
                "-file", certificatePath.toString(),
                "-noprompt"));
    }

    private static void runKeyTool(final List<String> command) throws IOException, InterruptedException {
        final var process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
        final var output = new String(process.getInputStream().readAllBytes());
        final int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command failed (" + exitCode + "): " + String.join(" ", command) + "\n" + output);
        }
    }
}
