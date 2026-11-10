/**
 * OpenData application, JavaFX presentation, execution and first-run setup.
 * Depends on API contracts, not on Common or provider implementation modules.
 * Provider JARs and Common are assembled by the executable launcher.
 *
 * @author Terry Curran
 * @since 4.0.0
 * @version 4.1.0
 */
module com.towermarsh.opendata.core {
    requires com.towermarsh.opendata.api;
    requires java.sql;
    requires java.logging;
    requires java.net.http;
    // HTTP server is needed by integration tests, not at application runtime.
    requires static jdk.httpserver;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.csv;
    requires org.apache.commons.cli;
    requires org.apache.commons.dbcp2;
    requires org.apache.commons.pool2;
    requires org.jsoup;
    requires com.microsoft.sqlserver.jdbc;
    requires static com.github.spotbugs.annotations;

    exports com.towermarsh.opendata;
    exports com.towermarsh.opendata.app;
    exports com.towermarsh.opendata.cli;
    exports com.towermarsh.opendata.config;
    exports com.towermarsh.opendata.core.config.model;
    exports com.towermarsh.opendata.core.database;
    exports com.towermarsh.opendata.core.database.audit;
    exports com.towermarsh.opendata.core.logging;
    exports com.towermarsh.opendata.core.plugin;
    exports com.towermarsh.opendata.discovery;
    exports com.towermarsh.opendata.download;
    exports com.towermarsh.opendata.etl;
    exports com.towermarsh.opendata.gui;
    exports com.towermarsh.opendata.model;
    exports com.towermarsh.opendata.parser;
    exports com.towermarsh.opendata.setup;

    opens com.towermarsh.opendata.gui to javafx.fxml;
}
