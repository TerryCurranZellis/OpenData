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
 * <p>
 * The dialogs are deliberately read-only. Configuration loading, file I/O and
 * database access happen outside this class on controller-owned JavaFX
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

    /**
     * Show properties.
     *
     * @param owner input owner
     * @param title input title
     * @param header input header
     * @param entries input entries
     */
    static void showProperties(
            Window owner,
            String title,
            String header,
            List<ConfigurationDisplayEntry> entries) {
        Objects.requireNonNull(entries, "entries");
        var dialog = createDialog(owner, title, ButtonType.OK);
        dialog.setHeaderText(header);

        var table = new TableView<ConfigurationDisplayEntry>();
        table.setEditable(false);
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefSize(920, 560);
        table.setPlaceholder(new Label("No values are available."));

        var propertyColumn = new TableColumn<ConfigurationDisplayEntry, String>("Property");
        propertyColumn.setCellValueFactory(data
                -> new ReadOnlyStringWrapper(data.getValue().property()));
        propertyColumn.setPrefWidth(300);
        propertyColumn.setMinWidth(190);

        var valueColumn = new TableColumn<ConfigurationDisplayEntry, String>("Value");
        valueColumn.setCellValueFactory(data
                -> new ReadOnlyStringWrapper(data.getValue().value()));
        valueColumn.setPrefWidth(600);
        valueColumn.setCellFactory(column -> wrappingCell(column));

        table.getColumns().addAll(propertyColumn, valueColumn);
        table.getItems().setAll(entries);
        dialog.getDialogPane().setContent(table);
        dialog.getDialogPane().setPrefSize(960, 660);
        dialog.showAndWait();
    }

    /**
     * Show log.
     *
     * @param owner input owner
     * @param snapshot input snapshot
     */
    static void showLog(Window owner, LogSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        showText(
                owner,
                "OpenData Log",
                snapshot.file().toString(),
                snapshot.content(),
                false,
                true);
    }

    /**
     * Show help.
     *
     * @param owner input owner
     */
    static void showHelp(Window owner) {
        showText(
                owner,
                "OpenData Help",
                "OpenData Processing Framework",
                OpenDataHelpContent.load(),
                true,
                false);
    }

    /**
     * Show about.
     *
     * @param owner input owner
     * @param information input information
     */
    static void showAbout(Window owner, ApplicationInfo information) {
        Objects.requireNonNull(information, "information");
        var dialog = createDialog(owner, "About OpenData", ButtonType.OK);
        dialog.setHeaderText(null);

        var content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(8, 18, 12, 18));

        var image = loadSplashImage();
        if (image != null) {
            final var imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setFitWidth(760);
            imageView.setFitHeight(400);
            content.getChildren().add(imageView);
        }

        var product = new Label(
                information.productName() + "  Version " + information.version());
        product.getStyleClass().add("about-product-title");

        var slogan = new Label(information.slogan());
        slogan.getStyleClass().add("about-slogan");

        var details = new TextArea(String.join(System.lineSeparator(),
                information.description(),
                "",
                information.runtime() + "(c)" + information.licence(),
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

    /**
     * Show text.
     *
     * @param owner input owner
     * @param title input title
     * @param header input header
     * @param content input content
     * @param wrapText input wrap text
     * @param logStyle input log style
     */
    private static void showText(
            Window owner,
            String title,
            String header,
            String content,
            boolean wrapText,
            boolean logStyle) {
        var dialog = createDialog(owner, title, CLOSE);
        dialog.setHeaderText(header);

        var textArea = new TextArea(Objects.requireNonNullElse(content, ""));
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

    /**
     * Creates dialog.
     *
     * @param owner input owner
     * @param title input title
     * @param buttonType input button type
     * @return result of create dialog
     */
    private static Dialog<ButtonType> createDialog(
            Window owner,
            String title,
            ButtonType buttonType) {
        var dialog = new Dialog<ButtonType>();
        dialog.setTitle(title);
        if (owner != null) {
            dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);
        } else {
            dialog.initModality(Modality.APPLICATION_MODAL);
        }
        dialog.setResizable(true);
        dialog.getDialogPane().getButtonTypes().add(buttonType);
        var stylesheet = OpenDataInformationDialogs.class.getResource(STYLESHEET);
        if (stylesheet != null) {
            dialog.getDialogPane().getStylesheets().add(stylesheet.toExternalForm());
        }
        return dialog;
    }

    /**
     * Wrapping cell.
     *
     * @param column input column
     * @return result of wrapping cell
     */
    private static TableCell<ConfigurationDisplayEntry, String> wrappingCell(
            TableColumn<ConfigurationDisplayEntry, String> column) {
        return new TableCell<>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(column.widthProperty().subtract(18));
                text.textProperty().bind(itemProperty());
            }

            /**
             * {@inheritDoc}
             *
             * @param item input item
             * @param empty input empty
             */
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : text);
                setText(null);
            }
        };
    }

    /**
     * Returns load splash image.
     *
     * @return result of load splash image
     */
    private static Image loadSplashImage() {
        try (var input = OpenDataInformationDialogs.class
                .getResourceAsStream(SPLASH_IMAGE)) {
            return input == null ? null : new Image(input);
        } catch (Exception exception) {
            return null;
        }
    }
}
