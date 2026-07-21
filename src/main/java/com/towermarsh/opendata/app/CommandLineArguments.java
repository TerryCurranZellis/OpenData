/*
 *  Filename: CommandLineArguments.java
 * 
 *  (C) Copyright Terry Curran 2026. All rights reserved
 * 
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 * 
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 * 
 *  The author may be contacted by email to the following address:
 * 
 *  terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.app;

import java.nio.file.Path;

/**
 * Represents command line options supplied to the application.
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
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
