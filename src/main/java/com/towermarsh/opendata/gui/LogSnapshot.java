/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Snapshot of the application log shown in the JavaFX log viewer.
 *
 * @param file source log file
 * @param content text read from the source file
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public record LogSnapshot(Path file, String content) {

    /** Validates components. */
    public LogSnapshot {
        file = Objects.requireNonNull(file, "file").toAbsolutePath().normalize();
        content = Objects.requireNonNullElse(content, "");
    }
}
