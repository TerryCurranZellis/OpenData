/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Displays the OpenData startup splash for at least four seconds.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class StartupSplashScreen {

    /**
     * Minimum time for which the startup splash remains visible.
     */
    public static final int MINIMUM_DISPLAY_MILLISECONDS = 4_000;

    /**
     * background colour
     */
    private static final Color BACKGROUND = new Color(2, 22, 57);

    /**
     * Window for display
     */
    private final AtomicReference<JWindow> window = new AtomicReference<>();

    /**
     * Shows the splash and schedules its dismissal after the minimum period.
     */
    public void show() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        runOnEventThread(() -> {
            if (window.get() != null) {
                return;
            }
            final var splash = new JWindow();
            splash.getContentPane().setLayout(new BorderLayout());
            splash.getContentPane().setBackground(BACKGROUND);

            final JLabel image = new JLabel(
                    OpenDataImageLoader.loadScaled(600), SwingConstants.CENTER);
            image.setBorder(BorderFactory.createLineBorder(new Color(35, 188, 238), 1));
            splash.add(image, BorderLayout.CENTER);
            splash.pack();
            splash.setLocationRelativeTo(null);
            window.set(splash);
            splash.setVisible(true);

            final var timer = new Timer(MINIMUM_DISPLAY_MILLISECONDS, event -> close());
            timer.setRepeats(false);
            timer.start();
        });
    }

    /**
     * Closes the splash when it is currently visible.
     */
    public void close() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        runOnEventThread(() -> {
            final JWindow splash = window.getAndSet(null);
            if (splash != null) {
                splash.setVisible(false);
                splash.dispose();
            }
        });
    }

    /**
     * starts the splash screen
     */
    private static void runOnEventThread(final Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
        } else {
            SwingUtilities.invokeLater(action);
        }
    }
}
