/*
 * Copyright Â© 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.app;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

/**
 * Supplies display information used by the splash and About windows.
 *
 * <p>
 * The implementation version is normally written into the JAR manifest by the
 * build. When the application is run from an IDE, the version falls back
 *
 * @param productName product name in this case OpenData
 * @param slogan slogan for application
 * @param version which version we are running
 * @param description more descriptive details
 * @param runtime run time version of java
 * @param licence license to the product
 * @param copyright copyright info
 * @author Terry Curran
 * @version 3.1.0
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
     *
     * @return the current application details
     */
    public static ApplicationInfo current() {
        final var applicationPackage = ApplicationInfo.class.getPackage();
        final var metadata = loadMetadata(ApplicationInfo.class.getClassLoader());
        final var implementationVersion = Optional
                .ofNullable(applicationPackage.getImplementationVersion())
                .filter(value -> !value.isBlank())
                .orElseGet(() -> metadata.getProperty("application.version", "development"));
        final String javaVersion = System.getProperty("java.version", "unknown");

        return new ApplicationInfo(
                metadata.getProperty("application.name", "OpenData"),
                "Transforming data for innovation",
                implementationVersion,
                "Downloads and transforms internet and file-based data, then loads it "
                + "into a local database for use by other projects.",
                "Java " + javaVersion,
                "Apache License 2.0",
                "Copyright Â© 2026 Terry Curran");
    }

    /**
     * Loads the metadata describing the produce
     *
     * @param classLoader load Java class
     * @return Java properties
     */
    private static Properties loadMetadata(final ClassLoader classLoader) {
        final var properties = new Properties();
        try (var input = classLoader.getResourceAsStream("application-metadata.properties")) {
            if (input != null) {
                try (var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }
            }
        } catch (IOException ignored) {
            // Metadata is optional; manifest and development fallbacks remain available.
        }
        return properties;
    }

}
