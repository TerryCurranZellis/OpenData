/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

/**
 * Tests shared filesystem digest helpers.
 *
 * @author Terry Curran
 * @version 3.3.0
 */
class PathDigestSupportTest {

    @Test
    void returnsFileNameAndSha256() throws IOException {
        final var file = Files.createTempFile("opendata-path-digest", ".txt");
        Files.writeString(file, "OpenData", StandardCharsets.UTF_8);

        try {
            assertEquals(file.getFileName().toString(), PathDigestSupport.fileName(file));
            assertEquals("a8b2c2298c7ef738d13822d92c4849171d190f23bc783af017fa5d1915b36977",
                    PathDigestSupport.sha256(file));
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
