/*
 * Filename: CommandLineArguments.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.app;

import java.nio.file.Path;

/**
 * Represents command line options supplied to the application.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class CommandLineArguments {

    private final Path configFile;

    /**
     * Creates command line arguments.
     *
     * @param configFile optional configuration file
     */
    public CommandLineArguments(
            Path configFile) {

        this.configFile = configFile;
    }

    /**
     * Returns the optional configuration file.
     *
     * @return configuration path
     */
    public Path getConfigFile() {

        return configFile;
    }

    /**
     * Parses command line arguments.
     *
     * @param args command line parameters
     * @return parsed arguments
     */
    public static CommandLineArguments parse(
            String[] args) {

        Path config = null;

        for (int i = 0;
                i < args.length;
                i++) {

            if ("--config".equals(args[i])
                    && i + 1 < args.length) {

                config
                        = Path.of(args[++i]);
            }
        }

        return new CommandLineArguments(config);
    }
}
