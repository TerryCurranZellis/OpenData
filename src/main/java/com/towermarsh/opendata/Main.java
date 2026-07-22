package com.towermarsh.opendata;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.towermarsh.opendata.cli.CommandLineArguments;
import com.towermarsh.opendata.cli.CommandLineArgumentsProcessor;
import com.towermarsh.opendata.cli.CommandLineProcessingException;
import com.towermarsh.opendata.config.ApplicationConfig;
import com.towermarsh.opendata.config.ConfigurationService;
import com.towermarsh.opendata.exception.ConfigurationException;

/**
 * Minimal integration example for command-line and configuration processing.
 */
public final class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private Main() {
    }

    public static void main(final String[] args) {
        final CommandLineArgumentsProcessor processor = new CommandLineArgumentsProcessor();

        try {
            final CommandLineArguments arguments = processor.parse(args);

            if (arguments.helpRequested()) {
                processor.printHelp(new PrintWriter(System.out, true));
                return;
            }
            if (arguments.versionRequested()) {
                System.out.println("OpenData Framework 0.1.0-SNAPSHOT");
                return;
            }
            if (arguments.listPluginsRequested()) {
                System.out.println("Installed plugins: ofgem");
                return;
            }

            final ApplicationConfig configuration =
                    new ConfigurationService().resolve(arguments);

            LOGGER.info(() -> "Selected plugin: " + configuration.pluginId());
            LOGGER.info(() -> "Dry run: " + configuration.dryRun());
            //LOGGER.info(() -> "Resolved properties: " + configuration.asMap().size());

            // Hand configuration to the plugin registry / pipeline engine here.
        } catch (CommandLineProcessingException exception) {
            System.err.println("Command-line error: " + exception.getMessage());
            processor.printHelp(new PrintWriter(System.err, true));
            System.exit(2);
        } catch (ConfigurationException exception) {
            LOGGER.log(Level.SEVERE, "Configuration error: {0}", exception.getMessage());
            System.exit(3);
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Unexpected application failure.", exception);
            System.exit(1);
        }
    }
}
