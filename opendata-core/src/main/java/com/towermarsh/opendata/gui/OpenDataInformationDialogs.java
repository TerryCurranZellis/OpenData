/*
 * Copyright Â© 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.app.ApplicationInfo;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * Custom JavaFX information dialogs used by the OpenData desktop interface.
 *
 * <p>The dialogs are deliberately read-only. Configuration loading, file I/O
 * and database access happen outside this class on controller-owned JavaFX
 * tasks.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
final class OpenDataInformationDialogs {

    private static final String STYLESHEET = "opendata-light.css";
    private static final String SPLASH_IMAGE = "opendata-splash.png";
    private static final ButtonType CLOSE = new ButtonType(
            "Close", ButtonBar.ButtonData.OK_DONE);

    private OpenDataInformationDialogs() {
    }

    static void showProperties(
            final Window owner,
            final String title,
            final String header,
            final List<ConfigurationDisplayEntry> entries) {
        Objects.requireNonNull(entries, "entries");
        final var dialog = createDialog(owner, title, ButtonType.OK);
        dialog.setHeaderText(header);

        final var table = new TableView<ConfigurationDisplayEntry>();
        table.setEditable(false);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefSize(920, 560);
        table.setPlaceholder(new Label("No values are available."));

        final var propertyColumn = new TableColumn<ConfigurationDisplayEntry, String>("Property");
        propertyColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().property()));
        propertyColumn.setPrefWidth(300);
        propertyColumn.setMinWidth(190);

        final var valueColumn = new TableColumn<ConfigurationDisplayEntry, String>("Value");
        valueColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().value()));
        valueColumn.setPrefWidth(600);
        valueColumn.setCellFactory(column -> wrappingCell(column));

        table.getColumns().addAll(propertyColumn, valueColumn);
        table.getItems().setAll(entries);
        dialog.getDialogPane().setContent(table);
        dialog.getDialogPane().setPrefSize(960, 660);
        dialog.showAndWait();
    }

    static void showLog(final Window owner, final LogSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        showText(
                owner,
                "OpenData Log",
                snapshot.file().toString(),
                snapshot.content(),
                false,
                true);
    }

    static void showHelp(final Window owner) {
        showText(
                owner,
                "OpenData Help",
                "OpenData Processing Framework",
                OpenDataHelpContent.load(),
                true,
                false);
    }

    static void showAbout(final Window owner, final ApplicationInfo information) {
        Objects.requireNonNull(information, "information");
        final var dialog = createDialog(owner, "About OpenData", ButtonType.OK);
        dialog.setHeaderText(null);

        final var content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(8, 18, 12, 18));

        final var image = loadSplashImage();
        if (image != null) {
            final var imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setFitWidth(760);
            imageView.setFitHeight(400);
            content.getChildren().add(imageView);
        }

        final var product = new Label(
                information.productName() + "  Version " + information.version());
        product.getStyleClass().add("about-product-title");

        final var slogan = new Label(information.slogan());
        slogan.getStyleClass().add("about-slogan");

        final var details = new TextArea(String.join(System.lineSeparator(),
                information.description(),
                "",
                information.runtime() + "  â€¢  " + information.licence(),
                information.copyright()));
        details.setEditable(false);
        details.setWrapText(true);
        details.setFocusTraversable(false);
        details.setPrefRowCount(5);
        details.setMaxWidth(Double.MAX_VALUE);
        details.getStyleClass().add("about-details");

        content.getChildren().addAll(product, slogan, details);
        VBox.setVgrow(details, Priority.ALWAYS);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefSize(840, 660);
        dialog.showAndWait();
    }

    private static void showText(
            final Window owner,
            final String title,
            final String header,
            final String content,
            final boolean wrapText,
            final boolean logStyle) {
        final var dialog = createDialog(owner, title, CLOSE);
        dialog.setHeaderText(header);

        final var textArea = new TextArea(Objects.requireNonNullElse(content, ""));
        textArea.setEditable(false);
        textArea.setWrapText(wrapText);
        textArea.setPrefColumnCount(110);
        textArea.setPrefRowCount(32);
        textArea.setFocusTraversable(true);
        if (logStyle) {
            textArea.getStyleClass().add("log-viewer");
        }

        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().setPrefSize(980, 680);
        dialog.showAndWait();
    }

    private static Dialog<ButtonType> createDialog(
            final Window owner,
            final String title,
            final ButtonType buttonType) {
        final var dialog = new Dialog<ButtonType>();
        dialog.setTitle(title);
        if (owner != null) {
            dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);
        } else {
            dialog.initModality(Modality.APPLICATION_MODAL);
        }
        dialog.setResizable(true);
        dialog.getDialogPane().getButtonTypes().add(buttonType);
        final var stylesheet = OpenDataInformationDialogs.class.getResource(STYLESHEET);
        if (stylesheet != null) {
            dialog.getDialogPane().getStylesheets().add(stylesheet.toExternalForm());
        }
        return dialog;
    }

    private static TableCell<ConfigurationDisplayEntry, String> wrappingCell(
            final TableColumn<ConfigurationDisplayEntry, String> column) {
        return new TableCell<>() {
            private final Text text = new Text();
            {
                text.wrappingWidthProperty().bind(column.widthProperty().subtract(18));
                text.textProperty().bind(itemProperty());
            }

            @Override
            protected void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : text);
                setText(null);
            }
        };
    }

    private static Image loadSplashImage() {
        try (var input = OpenDataInformationDialogs.class
                .getResourceAsStream(SPLASH_IMAGE)) {
            return input == null ? null : new Image(input);
        } catch (Exception exception) {
            return null;
        }
    }
}
