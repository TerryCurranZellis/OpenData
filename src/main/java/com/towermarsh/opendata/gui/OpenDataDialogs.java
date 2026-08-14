/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Common JavaFX dialogs used by the OpenData main window.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
final class OpenDataDialogs {

    private static final String APPLICATION_TITLE = "OpenData";

    private OpenDataDialogs() {
    }

    static void warning(
            final Window owner,
            final String header,
            final String message) {
        showAlert(Alert.AlertType.WARNING, owner, "Warning", header, message);
    }

    static void error(
            final Window owner,
            final String header,
            final String message) {
        showAlert(Alert.AlertType.ERROR, owner, "Error", header, message);
    }

    static void information(
            final Window owner,
            final String header,
            final String message) {
        showAlert(Alert.AlertType.INFORMATION, owner, APPLICATION_TITLE, header, message);
    }

    static boolean confirm(
            final Window owner,
            final String title,
            final String header,
            final String message) {
        final var alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                message,
                ButtonType.OK,
                ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(header);
        initOwner(alert, owner);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    /**
     * Opens a file chooser for one complete plugin definition.
     *
     * @param owner owning GUI window
     * @param preferredDirectories directories used to choose a useful initial location
     * @return selected file, or empty when cancelled
     */
    static Optional<Path> choosePluginDefinitionFile(
            final Window owner,
            final List<Path> preferredDirectories) {
        final var chooser = new FileChooser();
        chooser.setTitle("Register Plugin from File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        "OpenData plugin properties (*.properties)",
                        "*.properties"),
                new FileChooser.ExtensionFilter("All files", "*.*"));

        Optional.ofNullable(preferredDirectories)
                .stream()
                .flatMap(List::stream)
                .filter(Files::isDirectory)
                .findFirst()
                .ifPresent(directory -> chooser.setInitialDirectory(directory.toFile()));

        final var selected = chooser.showOpenDialog(owner);
        return selected == null
                ? Optional.empty()
                : Optional.of(selected.toPath().toAbsolutePath().normalize());
    }

    private static void showAlert(
            final Alert.AlertType type,
            final Window owner,
            final String title,
            final String header,
            final String message) {
        final var alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        initOwner(alert, owner);
        alert.showAndWait();
    }

    private static void initOwner(final Dialog<?> dialog, final Window owner) {
        if (owner != null) {
            dialog.initOwner(owner);
        }
    }

}
