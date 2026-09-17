/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.validation;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Reusable validation rules for configuration and transformed records.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class ValidationRules {

    private ValidationRules() {
        // Utility class.
    }

    /**
     * Requires non-blank text and returns its trimmed form.
     *
     * @param value value to validate
     * @param name field name used in error messages
     * @return trimmed value
     */
    public static String requireText(final String value, final String name) {
        return requireText(value, name, Integer.MAX_VALUE);
    }

    /**
     * Requires non-blank text no longer than the supplied maximum.
     *
     * @param value value to validate
     * @param name field name used in error messages
     * @param maximumLength maximum permitted length
     * @return trimmed value
     */
    public static String requireText(
            final String value,
            final String name,
            final int maximumLength) {
        Objects.requireNonNull(name, "name");
        if (maximumLength < 1) {
            throw new IllegalArgumentException("maximumLength must be positive");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        final String result = value.trim();
        if (result.length() > maximumLength) {
            throw new IllegalArgumentException(
                    name + " must not exceed " + maximumLength + " characters");
        }
        return result;
    }

    /**
     * Requires a positive duration.
     *
     * @param value duration to validate
     * @param name field name used in error messages
     * @return validated duration
     */
    public static Duration requirePositive(final Duration value, final String name) {
        Objects.requireNonNull(value, name);
        if (value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return value;
    }

    /**
     * Requires a non-negative integer.
     *
     * @param value value to validate
     * @param name field name used in error messages
     * @return validated value
     */
    public static int requireNonNegative(final int value, final String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
        return value;
    }

    /**
     * Requires an integer within an inclusive range.
     *
     * @param value value to validate
     * @param minimum inclusive minimum
     * @param maximum inclusive maximum
     * @param name field name used in error messages
     * @return validated value
     */
    public static int requireRange(
            final int value,
            final int minimum,
            final int maximum,
            final String name) {
        if (minimum > maximum) {
            throw new IllegalArgumentException("minimum must not exceed maximum");
        }
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(
                    name + " must be between " + minimum + " and " + maximum);
        }
        return value;
    }

    /**
     * Requires a decimal number within an inclusive range.
     *
     * @param value value to validate
     * @param minimum inclusive minimum
     * @param maximum inclusive maximum
     * @param name field name used in error messages
     * @return validated value
     */
    public static double requireRange(
            final double value,
            final double minimum,
            final double maximum,
            final String name) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(name + " must be a finite number");
        }
        if (minimum > maximum) {
            throw new IllegalArgumentException("minimum must not exceed maximum");
        }
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(
                    name + " must be between " + minimum + " and " + maximum);
        }
        return value;
    }

    /**
     * Requires the start date not to be after the end date.
     *
     * @param startDate inclusive start date
     * @param endDate inclusive end date
     * @param name range name used in error messages
     */
    public static void requireDateOrder(
            final LocalDate startDate,
            final LocalDate endDate,
            final String name) {
        Objects.requireNonNull(startDate, "startDate");
        Objects.requireNonNull(endDate, "endDate");
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(name + " start date must not be after its end date");
        }
    }
}
