/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.setup.InitialisationRequest;
import com.towermarsh.opendata.setup.InitialisationService;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Three-step JavaFX first-run wizard. Setup occurs on a worker thread so the
 * UI stays responsive while SQL Server tables and plugin schemas are created.
 * The main application is displayed only after successful completion.
 *
 * @author Terry Curran
 * @version 4.0.0
 */
public final class InitialisationWizard {

    private final Stage stage;
    private final Runnable completed;
    private final InitialisationService service;
    private final BorderPane root = new BorderPane();
    private final Label heading = new Label();
    private final Label guidance = new Label();
    private final Button back = new Button("Back");
    private final Button next = new Button("Next");
    private final Button cancel = new Button("Cancel");
    private final TextArea log = new TextArea();
    private final Label reviewContent = new Label();
    private final TextField host = new TextField("localhost");
    private final TextField port = new TextField("1433");
    private final TextField database = new TextField("OpenData");
    private final TextField adminLogin = new TextField("sa");
    private final PasswordField adminPassword = new PasswordField();
    private final TextField applicationLogin = new TextField("OpenData");
    private final PasswordField applicationPassword = new PasswordField();
    private final PasswordField confirmPassword = new PasswordField();
    private final TextField keyPath = new TextField(
            Path.of(System.getProperty("user.home"), ".opendata", "opendata-aes.key").toAbsolutePath().toString());
    private final CheckBox trustCertificate = new CheckBox("Trust a self-signed SQL Server certificate (local installations only)");
    private final AtomicBoolean busy = new AtomicBoolean();
    private final List<Node> pages;
    private int page;
    private boolean successful;

    /**
     * Creates a GUI wizard using the production setup service.
     *
     * @param stage JavaFX primary stage
     * @param completed action that opens the main application after setup
     */
    public InitialisationWizard(final Stage stage, final Runnable completed) {
        this(stage, completed, new InitialisationService());
    }

    /**
     * Creates a wizard with injectable setup implementation.
     *
     * @param stage primary stage
     * @param completed main-application action
     * @param service setup service
     */
    InitialisationWizard(
            final Stage stage, final Runnable completed, final InitialisationService service) {
        this.stage = Objects.requireNonNull(stage, "stage");
        this.completed = Objects.requireNonNull(completed, "completed");
        this.service = Objects.requireNonNull(service, "service");
        this.pages = List.of(connectionPage(), accountPage(), reviewPage(), progressPage());
    }

    /**
     * Displays the wizard; cancelling closes the application without clearing
     * the init flag or starting the main OpenData window.
     */
    public void show() {
        final var top = new VBox(5, heading, guidance);
        top.setPadding(new Insets(20));
        heading.setStyle("-fx-font-size: 21px; -fx-font-weight: bold;");
        guidance.setWrapText(true);
        root.setTop(top);
        root.setPadding(new Insets(12));
        final var buttons = new HBox(12, back, next, cancel);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(18));
        root.setBottom(buttons);
        back.setOnAction(event -> changePage(page - 1));
        next.setOnAction(event -> next());
        cancel.setOnAction(event -> stage.close());
        stage.setOnCloseRequest(event -> {
            if (busy.get()) {
                event.consume();
            }
        });
        stage.setTitle("OpenData 4.0.0 — Initial configuration");
        stage.setScene(new Scene(root, 690, 510));
        stage.setResizable(true);
        stage.show();
        changePage(0);
    }

    /**
     * Advances to the next validation step or executes the installation.
     */
    private void next() {
        try {
            if (successful) {
                applicationPassword.clear();
                confirmPassword.clear();
                adminPassword.clear();
                stage.setOnCloseRequest(null);
                completed.run();
                return;
            }
            if (page == 0) {
                connectionValidation();
                changePage(1);
            } else if (page == 1) {
                validatedRequest();
                changePage(2);
            } else if (page == 2) {
                runInstallation(validatedRequest());
            }
        } catch (IllegalArgumentException exception) {
            showError("Check the wizard entries", exception.getMessage());
        }
    }

    /**
     * Checks that a port number and administrator settings have been supplied.
     */
    private void connectionValidation() {
        if (host.getText().isBlank() || database.getText().isBlank()
                || adminLogin.getText().isBlank() || adminPassword.getText().isBlank()) {
            throw new IllegalArgumentException("Server, database and administrator credentials are required.");
        }
        parsePort();
    }

    /**
     * Validates the SQL TCP port number.
     *
     * @return parsed port
     */
    private int parsePort() {
        try {
            return Integer.parseInt(port.getText().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("The SQL Server TCP port must be a number.", exception);
        }
    }

    /**
     * Builds a validated immutable request using the current form values.
     *
     * @return install settings, including temporary administrator credentials
     */
    private InitialisationRequest validatedRequest() {
        if (!applicationPassword.getText().equals(confirmPassword.getText())) {
            throw new IllegalArgumentException("The two application passwords do not match.");
        }
        return new InitialisationRequest(host.getText(), parsePort(), database.getText(),
                adminLogin.getText(), adminPassword.getText(), applicationLogin.getText(),
                applicationPassword.getText(), Path.of(keyPath.getText()),
                trustCertificate.isSelected());
    }

    /**
     * Changes the page and updates the action buttons.
     *
     * @param destination zero-based wizard page
     */
    private void changePage(final int destination) {
        if (destination < 0 || destination >= pages.size()) {
            return;
        }
        page = destination;
        if (page == 2) {
            reviewContent.setText("Database: " + database.getText() + " on " + host.getText() + ":" + port.getText()
                    + "\nApplication login: " + applicationLogin.getText()
                    + "\nAES key file: " + keyPath.getText()
                    + "\nTLS: " + (trustCertificate.isSelected() ? "self-signed certificate trusted" : "certificate verified")
                    + "\n\nCreates the core tables, discovers and registers installed plugins, "
                    + "and disables every registered plugin.\n\n"
                    + "Existing database objects are retained on a retry.");
        }
        root.setCenter(pages.get(page));
        heading.setText(switch (page) {
            case 0 -> "Step 1 of 3 — Database server";
            case 1 -> "Step 2 of 3 — Application account and encryption";
            case 2 -> "Step 3 of 3 — Confirm installation";
            default -> "Configuring OpenData";
        });
        guidance.setText(switch (page) {
            case 0 -> "Use an SQL-authenticated administrator with CREATE DATABASE and CREATE LOGIN rights.";
            case 1 -> "The administrator password is not stored. The application password is encrypted with AES-256-GCM.";
            case 2 -> "The database and plugin tables will be created. All discovered plugins will remain disabled.";
            default -> "Do not close the application while SQL Server is being configured.";
        });
        back.setDisable(page == 0 || page == 3);
        next.setText(page == 2 ? "Initialise" : "Next");
        next.setDisable(page == 3);
        cancel.setText("Cancel");
    }

    /**
     * Creates the database connection form.
     *
     * @return step one form
     */
    private Node connectionPage() {
        final var grid = grid();
        line(grid, 0, "SQL Server host", host);
        line(grid, 1, "TCP port", port);
        line(grid, 2, "Database name", database);
        line(grid, 3, "Administrator login", adminLogin);
        line(grid, 4, "Administrator password", adminPassword);
        grid.add(trustCertificate, 1, 5);
        return grid;
    }

    /**
     * Creates application SQL login and AES key settings.
     *
     * @return step two form
     */
    private Node accountPage() {
        final var grid = grid();
        line(grid, 0, "Application SQL login", applicationLogin);
        line(grid, 1, "Application password", applicationPassword);
        line(grid, 2, "Confirm password", confirmPassword);
        final var browse = new Button("Browse...");
        browse.setOnAction(event -> chooseKeyFile());
        final var keyLine = new HBox(8, keyPath, browse);
        HBox.setHgrow(keyPath, Priority.ALWAYS);
        line(grid, 3, "External AES key file", keyLine);
        final var note = new Label("A new 256-bit key will be generated if the file does not exist. "
                + "Restrict file permissions; never add the key to Git.");
        note.setWrapText(true);
        grid.add(note, 1, 4);
        return grid;
    }

    /**
     * Lets the user choose a path for the AES secret key.
     */
    private void chooseKeyFile() {
        final var picker = new FileChooser();
        picker.setTitle("Select existing or new AES key filename");
        picker.setInitialFileName("opendata-aes.key");
        final File file = picker.showSaveDialog(stage);
        if (file != null) {
            keyPath.setText(file.toPath().toAbsolutePath().toString());
        }
    }

    /**
     * Creates a safe review page without passwords or encryption key content.
     *
     * @return review pane
     */
    private Node reviewPage() {
        reviewContent.setWrapText(true);
        final var wrapper = new VBox(20, reviewContent);
        wrapper.setPadding(new Insets(25));
        return wrapper;
    }

    /**
     * Creates the read-only, scrolling setup log view.
     *
     * @return progress pane
     */
    private Node progressPage() {
        log.setEditable(false);
        log.setWrapText(true);
        final var area = new VBox(8, new Label("Installation progress"), log);
        VBox.setVgrow(log, Priority.ALWAYS);
        area.setPadding(new Insets(10));
        return area;
    }

    /**
     * Schedules database provisioning on a JavaFX background Task.
     *
     * @param request validated first-run configuration
     */
    private void runInstallation(final InitialisationRequest request) {
        changePage(3);
        busy.set(true);
        cancel.setDisable(true);
        final Task<List<String>> work = new Task<>() {
            /** Performs database work and reports progress safely to JavaFX. */
            @Override
            protected List<String> call() {
                return service.initialise(request, message -> Platform.runLater(
                        () -> log.appendText(message + System.lineSeparator())));
            }
        };
        work.setOnSucceeded(event -> {
            busy.set(false);
            successful = true;
            log.appendText("Registered " + work.getValue().size() + " disabled plugin(s)." + System.lineSeparator());
            next.setText("Finish — Open OpenData");
            next.setDisable(false);
            cancel.setDisable(true);
        });
        work.setOnFailed(event -> {
            busy.set(false);
            cancel.setDisable(false);
            cancel.setText("Close");
            final var exception = work.getException();
            log.appendText("SETUP FAILED: " + exception.getMessage() + System.lineSeparator());
            log.appendText("init remains true. Correct the problem and restart the wizard." + System.lineSeparator());
        });
        final var worker = new Thread(work, "opendata-initialisation");
        worker.setDaemon(true);
        worker.start();
    }

    /**
     * Creates a common two-column, padded form grid.
     *
     * @return form grid
     */
    private static GridPane grid() {
        final var grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(14);
        grid.setPadding(new Insets(24));
        return grid;
    }

    /**
     * Adds a labelled field to a form grid.
     *
     * @param grid target grid
     * @param row target row number
     * @param label field label
     * @param input form control
     */
    private static void line(final GridPane grid, final int row,
            final String label, final Node input) {
        grid.add(new Label(label), 0, row);
        grid.add(input, 1, row);
        GridPane.setHgrow(input, Priority.ALWAYS);
        if (input instanceof TextField field) {
            field.setMaxWidth(Double.MAX_VALUE);
        }
    }

    /**
     * Displays validation errors without logging or disclosing passwords.
     *
     * @param title alert heading
     * @param detail error detail
     */
    private void showError(final String title, final String detail) {
        final var alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(detail);
        alert.showAndWait();
    }
}
