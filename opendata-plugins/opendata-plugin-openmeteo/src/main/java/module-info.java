module com.towermarsh.opendata.plugin.openmeteo {
    requires com.towermarsh.opendata.api;
    requires com.towermarsh.opendata.common;
    requires com.fasterxml.jackson.databind;
    requires java.logging;
    requires java.sql;
    requires java.net.http;
    exports com.towermarsh.opendata.plugin.openmeteo;
}
