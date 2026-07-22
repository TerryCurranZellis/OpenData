package com.towermarsh.opendata.cli;

/**
 * Represents invalid command-line syntax or incompatible options.
 */
public final class CommandLineProcessingException extends RuntimeException {

    public CommandLineProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
