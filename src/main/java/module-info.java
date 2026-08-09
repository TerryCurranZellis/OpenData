/*
 *  Filename: module-info.java
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

module OpenData {
    exports com.towermarsh.opendata;
    requires javafx.controls;
    requires javafx.fxml;
    opens com.towermarsh.opendata.gui to javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires java.sql;
    requires java.net.http;
    requires jdk.httpserver;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires org.apache.commons.csv;
    requires org.apache.commons.io;
    requires org.apache.commons.codec;
    requires org.apache.commons.cli;
    requires org.apache.commons.dbcp2;
    requires org.apache.commons.pool2;
    requires org.apache.commons.logging;
    requires java.transaction;
    requires org.jsoup;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires commons.math3;
    requires com.zaxxer.sparsebitset;
    requires org.apache.poi.ooxml.schemas;
    requires org.apache.xmlbeans;
    requires org.apache.commons.compress;
    requires org.apache.commons.lang3;
    requires com.github.virtuald.curvesapi;
    requires org.apache.logging.log4j;
    requires org.apache.commons.collections4;
    requires org.apache.logging.log4j.to.jul;
    requires com.microsoft.sqlserver.jdbc;
    requires org.apache.pdfbox;
    requires org.apache.pdfbox.io;
    requires org.apache.fontbox;
}
