/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.transform;

import com.towermarsh.opendata.model.DataFile;
import com.towermarsh.opendata.plugin.example.transform.model.ExampleRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Converts the downloaded example text file into typed records. */
public final class ExampleTransformer {

    public List<ExampleRecord> transform(final DataFile source)
            throws IOException {
        Objects.requireNonNull(source, "source");
        final var records = new ArrayList<ExampleRecord>();
        final var lines = Files.readAllLines(source.getFilePath());
        for (int index = 0; index < lines.size(); index++) {
            final String value = lines.get(index).trim();
            if (!value.isEmpty()) {
                records.add(new ExampleRecord(index + 1L, value));
            }
        }
        return List.copyOf(records);
    }
}
