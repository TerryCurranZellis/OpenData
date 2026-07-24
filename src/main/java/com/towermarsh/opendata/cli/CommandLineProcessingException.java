/*
 * Filename: CommandLineProcessingException.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.cli;

/** Raised when command-line arguments are invalid. */
public final class CommandLineProcessingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CommandLineProcessingException(final String message) {
        super(message);
    }

    public CommandLineProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
