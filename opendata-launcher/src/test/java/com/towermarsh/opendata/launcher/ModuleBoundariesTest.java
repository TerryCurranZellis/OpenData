/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.launcher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * Guards Maven and source boundaries between API, Common, Core and providers.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
class ModuleBoundariesTest {

    private static final Pattern DEPENDENCY = Pattern.compile(
            "(?s)<dependency>\\s*<groupId>\\s*(?:com\\.towermarsh|\\$\\{project\\.groupId\\})"
            + "\\s*</groupId>\\s*<artifactId>\\s*(opendata-[^<]+)\\s*</artifactId>");

    /**
     * Verifies that every module's declared Maven dependencies follow the rules.
     *
     * @throws IOException when a project file cannot be read
     */
    @Test
    void mavenDependenciesRespectModuleBoundaries() throws IOException {
        var root = repositoryRoot();
        checkPom(root.resolve("opendata-api/pom.xml"),
                Set.of("opendata-core", "opendata-common", "opendata-launcher"), true);
        checkPom(root.resolve("opendata-common/pom.xml"),
                Set.of("opendata-core", "opendata-launcher"), true);
        checkPom(root.resolve("opendata-core/pom.xml"),
                Set.of("opendata-common", "opendata-launcher"), true);

        try (var plugins = Files.newDirectoryStream(root.resolve("opendata-plugins"), "opendata-plugin-*")) {
            for (var plugin : plugins) {
                var pom = plugin.resolve("pom.xml");
                if (Files.isRegularFile(pom)) {
                    checkPom(pom, Set.of("opendata-core", "opendata-launcher"), false);
                }
            }
        }
    }

    /**
     * Verifies that Core does not require Common through the Java module system.
     *
     * @throws IOException when the module descriptor cannot be read
     */
    @Test
    void coreModuleDescriptorRequiresOnlyApi() throws IOException {
        var descriptor = Files.readString(repositoryRoot().resolve("opendata-core/src/main/java/module-info.java"));
        assertFalse(descriptor.contains("requires com.towermarsh.opendata.common"),
                "Core must not require Common");
    }

    /**
     * Catches forbidden source imports even when Maven dependencies are missing.
     *
     * @throws IOException when a source file cannot be read
     */
    @Test
    void productionSourcesDoNotImportForbiddenModules() throws IOException {
        var root = repositoryRoot();
        checkImports(root.resolve("opendata-api/src/main/java"),
                "import com.towermarsh.opendata.common.",
                "import com.towermarsh.opendata.core.");
        checkImports(root.resolve("opendata-common/src/main/java"),
                "import com.towermarsh.opendata.core.");
        checkImports(root.resolve("opendata-core/src/main/java"),
                "import com.towermarsh.opendata.common.",
                "import com.towermarsh.opendata.util.",
                "import com.towermarsh.opendata.validation.");
    }

    /**
     * Checks for accidental provider implementation imports in shared modules
     * and forbidden Core imports in provider implementations.
     *
     * @throws IOException when production sources cannot be read
     */
    @Test
    void providersAndSharedModulesDoNotImportForbiddenImplementations() throws IOException {
        var root = repositoryRoot();
        var providerClass = Pattern.compile(
                "^import\\s+(?:static\\s+)?com\\.towermarsh\\.opendata\\.plugin\\.[a-z][a-z0-9]*\\.");
        for (var module : List.of("opendata-api", "opendata-common", "opendata-core")) {
            checkMatchingImports(root.resolve(module + "/src/main/java"), providerClass);
        }
        var plugins = root.resolve("opendata-plugins");
        try (var directories = Files.newDirectoryStream(plugins, "opendata-plugin-*")) {
            for (var plugin : directories) {
                var sourceRoot = plugin.resolve("src/main/java");
                if (Files.isDirectory(sourceRoot)) {
                    checkImports(sourceRoot,
                            "import com.towermarsh.opendata.core.",
                            "import com.towermarsh.opendata.gui.",
                            "import com.towermarsh.opendata.app.");
                }
            }
        }
    }

    /**
     * Rejects imports matching a provider implementation package pattern.
     *
     * @param sourceRoot module Java source directory
     * @param forbiddenPattern forbidden Java import
     * @throws IOException when a source cannot be read
     */
    private static void checkMatchingImports(Path sourceRoot, Pattern forbiddenPattern) throws IOException {
        try (var sources = Files.walk(sourceRoot)) {
            for (var source : sources.filter(file -> file.toString().endsWith(".java")).toList()) {
                for (var line : Files.readAllLines(source)) {
                    assertFalse(forbiddenPattern.matcher(line.stripLeading()).find(),
                            () -> source + " illegally imports provider implementation: " + line);
                }
            }
        }
    }

    /**
     * Checks internal Maven dependencies for forbidden module identifiers.
     *
     * @param pom POM file to inspect
     * @param forbidden forbidden Maven artifacts
     * @param forbidProviders whether provider implementation dependencies are forbidden
     * @throws IOException when the POM cannot be read
     */
    private static void checkPom(Path pom, Set<String> forbidden, boolean forbidProviders) throws IOException {
        var matcher = DEPENDENCY.matcher(Files.readString(pom));
        while (matcher.find()) {
            var dependency = matcher.group(1);
            assertFalse(forbidden.contains(dependency)
                    || (forbidProviders && dependency.startsWith("opendata-plugin-")),
                    pom + " declares forbidden dependency " + dependency);
        }
    }

    /**
     * Checks Java source imports for illegal source dependencies.
     *
     * @param sourceRoot source directory
     * @param forbiddenPrefixes forbidden import prefixes
     * @throws IOException when source files cannot be read
     */
    private static void checkImports(Path sourceRoot, String... forbiddenPrefixes) throws IOException {
        try (var files = Files.walk(sourceRoot)) {
            for (var path : files.filter(file -> file.toString().endsWith(".java")).toList()) {
                for (var line : Files.readAllLines(path)) {
                    var trimmed = line.stripLeading();
                    for (var prefix : forbiddenPrefixes) {
                        assertFalse(trimmed.startsWith(prefix),
                                path + " imports forbidden package: " + trimmed);
                    }
                }
            }
        }
    }

    /**
     * Finds the reactor root from an IDE or the Maven launcher project directory.
     *
     * @return project root containing all Maven modules
     */
    private static Path repositoryRoot() {
        var current = Path.of("").toAbsolutePath().normalize();
        while (current != null && !Files.isRegularFile(current.resolve("opendata-api/pom.xml"))) {
            current = current.getParent();
        }
        assertNotNull(current, "OpenData multi-module repository root was not found");
        return current;
    }
}
