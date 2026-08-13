/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ui;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * Loads and scales the packaged OpenData splash image.
 *
 * @deprecated retained temporarily for compatibility; new desktop UI code uses JavaFX.
 * This class is scheduled for removal after the JavaFX dialog migration.
 * @author Terry Curran
 * @version 3.1.0
 */
@Deprecated(since = "3.1.0")
final class OpenDataImageLoader {

    /**
     * location of the image
     */
    private static final String IMAGE_RESOURCE
            = "/com/towermarsh/opendata/ui/opendata-splash.png";

    /**
     * Instantiate process
     */
    private OpenDataImageLoader() {
    }

    /**
     * Load the image
     *
     * @param maxwidth maximum width of image
     */
    static ImageIcon loadScaled(final int maximumWidth) {
        final var resource = OpenDataImageLoader.class.getResource(IMAGE_RESOURCE);
        if (resource == null) {
            throw new IllegalStateException("Splash image resource is missing: " + IMAGE_RESOURCE);
        }
        final var source = new ImageIcon(resource);
        if (source.getIconWidth() <= maximumWidth) {
            return source;
        }
        final var scale = (double) maximumWidth / source.getIconWidth();
        final var height = Math.max(1, (int) Math.round(source.getIconHeight() * scale));
        final var scaled = source.getImage().getScaledInstance(
                maximumWidth, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
