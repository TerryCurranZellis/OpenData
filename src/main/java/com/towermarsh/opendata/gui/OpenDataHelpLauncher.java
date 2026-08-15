/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Window;

/**
 * Opens the compiled Windows HTML Help file when it is available and falls
 * back to the built-in JavaFX help viewer otherwise.
 *
 * <p>The resolver supports both a source-tree execution and a jpackage image.
 * The packaged form expects the CHM below an {@code app/help} directory beside
 * the application JAR.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
final class OpenDataHelpLauncher {

    private static final Logger LOGGER = Logger.getLogger(OpenDataHelpLauncher.class.getName());

    static final String HELP_FILE_NAME = "OpenData-Technical-User-Guide.chm";

    private OpenDataHelpLauncher() {
        // Utility class.
    }

    /**
     * Shows OpenData help using compiled Windows Help when possible.
     *
     * @param owner owner of the JavaFX fallback dialog
     */
    static void show(final Window owner) {
        if (isWindows(System.getProperty("os.name", ""))) {
            final var helpFile = locateHelpFile(defaultRoots());
            if (helpFile.isPresent()) {
                try {
                    new ProcessBuilder("hh.exe", helpFile.orElseThrow().toString()).start();
                    return;
                } catch (IOException exception) {
                    LOGGER.log(Level.WARNING,
                            "Unable to open compiled Windows Help; using the JavaFX help viewer instead: {0}",
                            exception.getMessage());
                    LOGGER.log(Level.FINE, "Compiled Help launch failure details.", exception);
                }
            }
        }
        OpenDataInformationDialogs.showHelp(owner);
    }

    /**
     * Finds the first compiled Help file below the supplied roots.
     *
     * @param roots candidate roots in priority order
     * @return resolved Help file, or empty when none exists
     */
    static Optional<Path> locateHelpFile(final List<Path> roots) {
        Objects.requireNonNull(roots, "roots");
        for (Path root : roots) {
            if (root == null) {
                continue;
            }
            final var normalised = root.toAbsolutePath().normalize();
            final var direct = normalised.resolve("help").resolve(HELP_FILE_NAME);
            if (Files.isRegularFile(direct)) {
                return Optional.of(direct);
            }
            final var generated = normalised
                    .resolve("docs")
                    .resolve("build")
                    .resolve("help")
                    .resolve("TechnicalUserGuide")
                    .resolve(HELP_FILE_NAME);
            if (Files.isRegularFile(generated)) {
                return Optional.of(generated);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns whether the supplied operating-system name represents Windows.
     *
     * @param osName operating-system name
     * @return true for Windows
     */
    static boolean isWindows(final String osName) {
        return Objects.requireNonNullElse(osName, "")
                .toLowerCase(Locale.ROOT)
                .startsWith("windows");
    }

    private static List<Path> defaultRoots() {
        final Set<Path> roots = new LinkedHashSet<>();
        roots.add(Path.of(System.getProperty("user.dir", ".")));

        applicationCodeLocation().ifPresent(location -> {
            roots.add(location);
            final var parent = location.getParent();
            if (parent != null) {
                roots.add(parent);
            }
        });
        return new ArrayList<>(roots);
    }

    private static Optional<Path> applicationCodeLocation() {
        try {
            final var codeSource = OpenDataHelpLauncher.class
                    .getProtectionDomain()
                    .getCodeSource();
            if (codeSource == null || codeSource.getLocation() == null) {
                return Optional.empty();
            }
            final var location = Path.of(codeSource.getLocation().toURI())
                    .toAbsolutePath()
                    .normalize();
            return Optional.of(Files.isDirectory(location)
                    ? location
                    : location.getParent());
        } catch (URISyntaxException | RuntimeException exception) {
            LOGGER.log(Level.FINE, "Unable to resolve application code location.", exception);
            return Optional.empty();
        }
    }
}
