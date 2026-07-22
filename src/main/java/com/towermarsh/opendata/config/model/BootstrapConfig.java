package com.towermarsh.opendata.config.model;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * Application settings required before a plugin definition can be loaded.
 *
 * @param applicationName application name
 * @param environment runtime environment name
 * @param workingDirectory working directory
 * @param archiveDirectory archive directory
 * @param failedDirectory failed-download directory
 * @param values additional bootstrap values
 */
public record BootstrapConfig(
        String applicationName,
        String environment,
        Path workingDirectory,
        Path archiveDirectory,
        Path failedDirectory,
        Map<String, String> values) {

    public BootstrapConfig {
        Objects.requireNonNull(applicationName, "applicationName");
        Objects.requireNonNull(environment, "environment");
        Objects.requireNonNull(workingDirectory, "workingDirectory");
        Objects.requireNonNull(archiveDirectory, "archiveDirectory");
        Objects.requireNonNull(failedDirectory, "failedDirectory");
        values = Map.copyOf(Objects.requireNonNull(values, "values"));
    }
}
