/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import static java.awt.GraphicsEnvironment.isHeadless;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import static javax.swing.SwingUtilities.invokeAndWait;
import static javax.swing.SwingUtilities.isEventDispatchThread;

/**
 * Displays version and product information in a modal About window.
 *
 * @deprecated retained temporarily for compatibility; new desktop UI code uses JavaFX.
 * This class is scheduled for removal after the JavaFX dialog migration.
 * @author Terry Curran
 * @version 3.1.0
 */
@Deprecated(since = "3.1.0")
@SuppressWarnings("deprecation")
public final class AboutDialog {

    /**
     * set background colour
     */
    private static final Color BACKGROUND = new Color(2, 22, 57);

    /**
     * set text colour
     */
    private static final Color TEXT = new Color(238, 247, 255);

    /**
     * set accent colour
     */
    private static final Color ACCENT = new Color(35, 188, 238);

    /**
     * Instantiate
     */
    private AboutDialog() {
    }

    /**
     * Shows the About window and waits until the user presses OK.
     *
     * @param information application information to display
     */
    public static void showAndWait(final ApplicationInfo information) {
        if (isHeadless()) {
            printToConsole(information);
            return;
        }
        final Runnable display = () -> createDialog(information).setVisible(true);
        if (isEventDispatchThread()) {
            display.run();
            return;
        }
        try {
            invokeAndWait(display);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException exception) {
            throw new IllegalStateException("Unable to display the About window.", exception.getCause());
        }
    }

    /**
     * Setup and display the about window
     *
     * @param information application information
     */
    private static JDialog createDialog(final ApplicationInfo information) {
        final var dialog = new JDialog((Window) null, "About OpenData",
                JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND);
        final var image = new JLabel(
                OpenDataImageLoader.loadScaled(960), SwingConstants.CENTER);
        dialog.add(image, BorderLayout.CENTER);
        dialog.add(createInformationPanel(dialog, information), BorderLayout.SOUTH);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(750, 580));
        dialog.setLocationRelativeTo(null);
        return dialog;
    }

    /**
     * create a text panel to display applicationn information
     *
     * @param dialog the dialog box
     * @param information the application information
     */
    private static JPanel createInformationPanel(
            final JDialog dialog,
            final ApplicationInfo information) {
        final var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ACCENT),
                BorderFactory.createEmptyBorder(14, 24, 16, 24)));

        panel.add(label("Version " + information.version(), Font.BOLD, 16));
        panel.add(Box.createVerticalStrut(7));
        panel.add(label("<html><div style='text-align:center;width:720px'>"
                + information.description() + "</div></html>", Font.PLAIN, 13));
        panel.add(Box.createVerticalStrut(8));
        panel.add(label(information.runtime() + "  •  " + information.licence(), Font.PLAIN, 12));
        panel.add(Box.createVerticalStrut(3));
        panel.add(label(information.copyright(), Font.PLAIN, 12));
        panel.add(Box.createVerticalStrut(14));

        final var ok = new JButton("OK");
        ok.setName("aboutOkButton");
        ok.setAlignmentX(JButton.CENTER_ALIGNMENT);
        ok.addActionListener(event -> dialog.dispose());
        panel.add(ok);
        return panel;
    }

    /**
     * create a label to display the information
     *
     * @param text text to display
     * @param style text style
     * @patam size text size
     */
    private static JLabel label(final String text, final int style, final int size) {
        final var label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(style, size));
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * display the information on the console window
     *
     * @param information application information
     */
    private static void printToConsole(final ApplicationInfo information) {
        System.out.printf("%s %s%n%s%n%s%n%s%n%s%n",
                information.productName(), information.version(), information.slogan(),
                information.description(), information.runtime(), information.licence());
    }
}
