/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ui;

import java.util.Optional;

/**
 * Supplies display information used by the splash and About windows.
 *
 * <p>The implementation version is normally written into the JAR manifest by
 * the build. When the application is run from an IDE, the version falls back
 * to {@code development}.</p>
 *
 * @author Terry Curran
 * @version 28 July 2026
 */
public record ApplicationInfo(
        String productName,
        String slogan,
        String version,
        String description,
        String runtime,
        String licence,
        String copyright) {

    /** 
     * Creates information for the currently running OpenData application. 
     */
    public static ApplicationInfo current() {
        final var applicationPackage = ApplicationInfo.class.getPackage();
        final var implementationVersion = Optional
                .ofNullable(applicationPackage.getImplementationVersion())
                .filter(value -> !value.isBlank())
                .orElse("development");
        final String javaVersion = System.getProperty("java.version", "unknown");

        return new ApplicationInfo(
                "OpenData",
                "Transforming data for innovation",
                implementationVersion,
                "Downloads and transforms internet and file-based data, then loads it "
                        + "into a local database for use by other projects.",
                "Java " + javaVersion,
                "Apache License 2.0",
                "Copyright © 2026 Terry Curran");
    }
}
