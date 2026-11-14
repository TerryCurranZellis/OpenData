/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.etl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Objects;

/**
 * Performs transformation of extracted data.
 *
 * @author Terry Curran
 * @version 4.2.0
 */
public class TransformService {

    /**
     * Cleans and prepares records.
     *
     * @param records input records
     * @return transformed records
     */
    public List<Map<String, String>> transform(
            List<Map<String, String>> records) {

        return mapRecords(records, this::cleanRecord);
    }

    /**
     * Applies a reusable, type-safe transformation to source records.
     *
     * <p>The mapping supports different input and output types, including
     * subtype collections and mappings accepting supertypes. As with the
     * original tabular transform, the returned list remains mutable.</p>
     *
     * @param <S> input record type
     * @param <T> output record type
     * @param records input records
     * @param mapping transformation performed on each record
     * @return transformed records in input order
     */
    public <S, T> List<T> mapRecords(
            List<? extends S> records,
            Function<? super S, ? extends T> mapping) {
        Objects.requireNonNull(records, "records");
        Objects.requireNonNull(mapping, "mapping");
        return records.stream().map(mapping).collect(Collectors.toList());
    }

    /**
     * Clean record.
     *
     * @param record input record
     * @return result of clean record
     */
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
