/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Splits SQL Server setup scripts at standalone GO batch separators.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
public final class SqlBatchParser {

    private static final Pattern GO = Pattern.compile("(?im)^\\s*GO\\s*$");
    private static final Pattern COMMENTS = Pattern.compile("(?s)/\\*.*?\\*/|--[^\\r\\n]*");

    private SqlBatchParser() {
    }

    /**
     * Returns executable non-empty batches in script order.
     *
     * @param script SQL Server script text
     * @return immutable list of executable batches
     */
    public static List<String> batches(String script) {
        var result = new ArrayList<String>();
        for (var batch : GO.split(Objects.requireNonNull(script, "script"))) {
            if (!COMMENTS.matcher(batch).replaceAll("").isBlank()) {
                result.add(batch.strip());
            }
        }
        return List.copyOf(result);
    }
}
