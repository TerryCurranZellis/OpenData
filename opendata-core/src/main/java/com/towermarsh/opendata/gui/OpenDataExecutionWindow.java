/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.plugin.PluginExecutionSummary;
import java.util.List;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Modal JavaFX window that displays live Execute or Dry-run JUL output.
 *
 * <p>The Close button and window close decoration are disabled while execution
 * is active. After the background task completes, the window remains open so
 * the operator can review the complete scrollable log before closing it.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
final class OpenDataExecutionWindow {

    private final Stage stage = new Stage();
    private final TextArea logArea = new TextArea();
    private final Label statusLabel = new Label("Starting...");
    private final Button closeButton = new Button("Close");
    private boolean running = true;

    OpenDataExecutionWindow(
            final Window owner,
            final String operationName,
            final List<String> pluginIds) {
        Objects.requireNonNull(operationName, "operationName");
        final var selected = List.copyOf(Objects.requireNonNull(pluginIds, "pluginIds"));

        stage.setTitle("OpenData — " + operationName);
        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
        } else {
            stage.initModality(Modality.APPLICATION_MODAL);
        }

        final var heading = new Label(operationName + " selected plugin"
                + (selected.size() == 1 ? "" : "s"));
        heading.getStyleClass().add("section-title");
        final var plugins = new Label(String.join(", ", selected));
        plugins.setWrapText(true);

        final var header = new VBox(4.0, heading, plugins, statusLabel);
        header.setPadding(new Insets(12.0, 12.0, 8.0, 12.0));

        logArea.setEditable(false);
        logArea.setWrapText(false);
        logArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 10pt;");
        logArea.setPromptText("Execution log messages will appear here...");

        closeButton.setDisable(true);
        closeButton.setPrefWidth(100.0);
        closeButton.setOnAction(event -> stage.close());
        final var buttons = new HBox(closeButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10.0, 12.0, 12.0, 12.0));

        final var root = new BorderPane(logArea, header, null, buttons, null);
        final var scene = new Scene(root, 1040.0, 620.0);
        final var stylesheet = OpenDataExecutionWindow.class.getResource("opendata-light.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
        stage.setScene(scene);
        stage.setMinWidth(760.0);
        stage.setMinHeight(460.0);
        stage.setOnCloseRequest(event -> {
            if (running) {
                event.consume();
            }
        });
    }

    void show() {
        stage.show();
    }

    void appendLog(final String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        logArea.appendText(text);
        logArea.positionCaret(logArea.getLength());
    }

    void complete(final PluginExecutionSummary summary) {
        Objects.requireNonNull(summary, "summary");
        running = false;
        statusLabel.setText(summary.allSuccessful()
                ? "Completed successfully — %d plugin%s processed."
                        .formatted(summary.results().size(), summary.results().size() == 1 ? "" : "s")
                : "Completed with failures — %d succeeded, %d failed or cancelled."
                        .formatted(summary.succeeded(), summary.failed()));
        closeButton.setDisable(false);
    }

    void fail(final String message) {
        running = false;
        statusLabel.setText("Execution could not be completed.");
        appendLog(System.lineSeparator()
                + "Execution error: " + Objects.toString(message, "Unknown error")
                + System.lineSeparator());
        closeButton.setDisable(false);
    }
}
