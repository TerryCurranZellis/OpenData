/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.launcher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.junit.jupiter.api.Test;

/**
 * Verifies author/version metadata and package documentation in the source tree.
 *
 * <p>The scan includes compiled modules, Java tests, and non-compiled plugin
 * examples/templates so new source files cannot silently omit required Javadocs.</p>
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
class JavadocInventoryTest {

    /**
     * Verifies required author/version tags on every Java source file.
     *
     * @throws IOException when a source file cannot be read
     */
    @Test
    void everyJavaSourceHasAuthorAndVersion() throws IOException {
        var root = repositoryRoot();
        try (var files = Files.walk(root)) {
            var sources = files.filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> isSourceTree(path.toString().replace('\\', '/')))
                    .toList();
            assertFalse(sources.isEmpty(), "No Java source files were found");
            for (var source : sources) {
                var contents = Files.readString(source);
                assertTrue(contents.contains("@author Terry Curran"),
                        () -> "Missing @author Terry Curran: " + root.relativize(source));
                assertTrue(contents.contains("@version "),
                        () -> "Missing Javadoc @version: " + root.relativize(source));
            }
        }
    }

    /**
     * Verifies a package-info file exists for every documented production package.
     *
     * @throws IOException when the source tree cannot be traversed
     */
    @Test
    void everyProductionPackageHasPackageInfo() throws IOException {
        var root = repositoryRoot();
        try (var files = Files.walk(root)) {
            for (var source : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                var filename = source.getFileName().toString();
                if (!isMainSourceTree(source.toString().replace('\\', '/'))
                        || "module-info.java".equals(filename)
                        || "package-info.java".equals(filename)) {
                    continue;
                }
                var descriptor = source.getParent().resolve("package-info.java");
                assertTrue(Files.isRegularFile(descriptor),
                        () -> "Missing package inventory for " + root.relativize(source));
            }
        }
    }

    /**
     * Checks a path belongs to a Java production or test source set.
     *
     * @param path normalised path
     * @return true for Java source-set paths
     */
    private static boolean isSourceTree(String path) {
        return isMainSourceTree(path) || path.contains("/src/test/java/");
    }

    /**
     * Checks whether the path belongs to a production Java source set.
     *
     * @param path normalised path
     * @return true for production sources, including example and template code
     */
    private static boolean isMainSourceTree(String path) {
        return path.contains("/src/main/java/");
    }

    /**
     * Finds the multi-module repository root for Maven and IDE test execution.
     *
     * @return the OpenData repository root
     */
    private static Path repositoryRoot() {
        var current = Path.of("").toAbsolutePath().normalize();
        while (current != null && !Files.isRegularFile(current.resolve("opendata-api/pom.xml"))) {
            current = current.getParent();
        }
        return Objects.requireNonNull(current, "OpenData Maven reactor root was not found");
    }
}
