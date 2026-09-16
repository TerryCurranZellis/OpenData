/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Locates plugin definition property files in OpenData configuration folders.
 *
 * <p>
 * The deployment-style folder {@code config/plugins} is checked first. The
 * source-tree folder {@code src/main/resources/config/plugins} is also checked
 * so the same GUI operation works while OpenData is being run directly from a
 * development checkout. The classpath index file is deliberately ignored: GUI
 * registration discovers complete {@code *.properties} definitions
 * directly.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public final class PluginConfigurationDirectoryScanner {

    private static final String INDEX_FILE = "index.properties";

    private final List<Path> directories;

    /**
     * Creates a scanner using the standard runtime and development locations.
     */
    public PluginConfigurationDirectoryScanner() {
        this(defaultDirectories());
    }

    /**
     * Creates a scanner using explicit directories, primarily for tests.
     *
     * @param directories configuration directories in search order
     */
    public PluginConfigurationDirectoryScanner(final List<Path> directories) {
        Objects.requireNonNull(directories, "directories");
        this.directories = directories.stream()
                .map(path -> Objects.requireNonNull(path, "directory")
                        .toAbsolutePath().normalize())
                .distinct()
                .toList();
    }

    /**
     * Returns the directories searched for plugin definitions.
     *
     * @return immutable directory list
     */
    public List<Path> directories() {
        return List.copyOf(new ArrayList<>(directories));
    }

    /**
     * Finds property files in the configured directories.
     *
     * <p>
     * Files are returned deterministically by directory search order and
     * filename. A physical path encountered more than once is returned only
     * once.</p>
     *
     * @return immutable list of plugin property files
     */
    public List<Path> scan() {
        final var result = new LinkedHashSet<Path>();
        for (var directory : directories) {
            if (!Files.isDirectory(directory)) {
                continue;
            }
            try (var files = Files.list(directory)) {
                files.filter(Files::isRegularFile)
                        .filter(PluginConfigurationDirectoryScanner::isPluginPropertiesFile)
                        .sorted()
                        .map(path -> path.toAbsolutePath().normalize())
                        .forEach(result::add);
            } catch (IOException exception) {
                throw new OpenDataConfigurationException(
                        "Unable to scan plugin configuration directory: " + directory,
                        exception);
            }
        }
        return List.copyOf(result);
    }

    private static boolean isPluginPropertiesFile(final Path path) {
        final var fileName = path.getFileName();
        if (fileName == null) {
            return false;
        }
        final var filename = fileName.toString().toLowerCase(Locale.ROOT);
        return filename.endsWith(".properties")
                && !INDEX_FILE.equals(filename);
    }

    private static List<Path> defaultDirectories() {
        final var workingDirectory = Path.of(System.getProperty("user.dir"))
                .toAbsolutePath().normalize();
        final List<Path> result = new ArrayList<>();
        result.add(workingDirectory.resolve("config").resolve("plugins"));
        result.add(workingDirectory.resolve("src").resolve("main").resolve("resources")
                .resolve("config").resolve("plugins"));
        return List.copyOf(result);
    }
}
