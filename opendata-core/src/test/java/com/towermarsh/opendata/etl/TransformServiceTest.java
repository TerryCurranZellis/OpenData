/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.etl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Verifies generic record mapping and existing tabular cleaning semantics.
 *
 * @author Terry Curran
 * @since 4.2.0
 * @version 4.2.0
 */
class TransformServiceTest {

    @Test
    void mapsRecordsBetweenDifferentTypes() {
        var records = List.of(2, 4, 6);
        List<String> result = new TransformService().mapRecords(
                records, value -> "row-" + value);
        assertEquals(List.of("row-2", "row-4", "row-6"), result);
    }

    @Test
    void trimsStringValuesAndReplacesNulls() {
        var record = new LinkedHashMap<String, String>();
        record.put("name", "  Example ");
        record.put("notes", null);
        List<Map<String, String>> result = new TransformService().transform(List.of(record));
        assertEquals("Example", result.getFirst().get("name"));
        assertEquals("", result.getFirst().get("notes"));
    }
}
