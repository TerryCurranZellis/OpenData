module com.towermarsh.opendata.core {
    requires com.fasterxml.jackson.databind;
    requires com.towermarsh.opendata.api;
    requires com.towermarsh.opendata.common;
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.httpserver;
    requires java.logging;
    requires java.net.http;
    requires java.sql;
    requires org.apache.commons.cli;
    requires org.apache.commons.csv;
    requires org.apache.commons.dbcp2;
    requires org.apache.commons.pool2;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires org.jsoup;
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

    opens com.towermarsh.opendata.gui to javafx.fxml;
}
