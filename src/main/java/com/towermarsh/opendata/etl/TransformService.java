/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.etl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Performs transformation of extracted data.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class TransformService {

    /**
     * Cleans and prepares records.
     *
     * @param records input records
     * @return transformed records
     */
    public List<Map<String, String>> transform(
            List<Map<String, String>> records) {

        return records.stream()
                .map(this::cleanRecord)
                .collect(Collectors.toList());
    }

    private Map<String, String> cleanRecord(
            Map<String, String> record) {

        record.replaceAll(
                (key, value)
                -> value == null
                        ? ""
                        : value.trim());

        return record;
    }
}
