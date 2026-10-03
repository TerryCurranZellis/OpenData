module com.towermarsh.opendata.common {
    requires transitive com.towermarsh.opendata.api;
    requires java.net.http;
    requires java.sql;
    requires org.jsoup;

    exports com.towermarsh.opendata.common.database;
    exports com.towermarsh.opendata.common.database.jdbc;
    exports com.towermarsh.opendata.common.logging;
    exports com.towermarsh.opendata.download.strategy;
    exports com.towermarsh.opendata.exception;
    exports com.towermarsh.opendata.util;
    exports com.towermarsh.opendata.validation;
}
