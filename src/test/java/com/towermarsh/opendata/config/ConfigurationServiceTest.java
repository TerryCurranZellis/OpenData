package com.towermarsh.opendata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.towermarsh.opendata.cli.CommandLineArguments;

class ConfigurationServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void overrideFileTakesPrecedenceOverPluginDefaults() throws Exception {
        final Path overrideFile = temporaryDirectory.resolve("ofgem.properties");
        Files.writeString(overrideFile, """
                database.batch-size=250
                application.working-directory=local-work
                """);

        final CommandLineArguments arguments = CommandLineArguments.builder()
                .pluginId("ofgem")
                .overrideFile(overrideFile)
                .dryRun(true)
                .build();

        final ApplicationConfig configuration =
                new ConfigurationService().resolve(arguments);

        assertEquals("ofgem", configuration.pluginId());
        assertEquals(
                250,
                Integer.parseInt(configuration.bootstrap().values().get("database.batch-size")));
        assertEquals(Path.of("local-work"), configuration.bootstrap().workingDirectory());
        assertTrue(configuration.dryRun());
    }
}
