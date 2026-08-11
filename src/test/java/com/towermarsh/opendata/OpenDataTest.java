/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata;

import com.towermarsh.opendata.cli.CommandLineArguments;
import com.towermarsh.opendata.cli.PluginCommand;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests OpenData startup logging decisions. */
class OpenDataTest {

    @Test
    void suppressesInvocationLoggingForGuiLaunches() {
        final var arguments = new CommandLineArguments(
                List.of(),
                false,
                Optional.empty(),
                OptionalInt.empty(),
                false,
                false,
                false,
                false,
                false,
                false,
                true,
                PluginCommand.GUI);

        assertFalse(OpenData.shouldLogInvocation(arguments));
    }

    @Test
    void keepsInvocationLoggingForPluginRuns() {
        final var arguments = new CommandLineArguments(
                List.of("openmeteo"),
                false,
                Optional.empty(),
                OptionalInt.empty(),
                false,
                true,
                false,
                false,
                false,
                false,
                false,
                PluginCommand.RUN);

        assertTrue(OpenData.shouldLogInvocation(arguments));
    }
}
