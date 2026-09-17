/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.validation;

/**
 * Converts a textual configuration value into a typed value.
 *
 * @param <T> parsed value type
 *
 * @author Terry Curran
 * @version 2.0.0
 */
@FunctionalInterface
public interface ValueParser<T> {

    /**
     * Parses one textual value.
     *
     * @param value textual value
     * @return parsed value
     * @throws Exception when the value cannot be parsed
     */
    T parse(String value) throws Exception;
}
