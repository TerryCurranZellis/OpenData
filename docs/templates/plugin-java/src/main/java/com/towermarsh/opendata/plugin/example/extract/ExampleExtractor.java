/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.extract;

import com.towermarsh.opendata.model.DataFile;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/** Reads the downloaded example representation without applying business rules. */
public final class ExampleExtractor {

    public List<String> extract(final DataFile source) throws IOException {
        Objects.requireNonNull(source, "source");
        return List.copyOf(Files.readAllLines(source.getFilePath()));
    }
}
