/**
 * OpenMeteo plugin for downloading historical daily weather observations from
 * the Open-Meteo archive API.
 *
 * <p>The plugin identifier intentionally follows the requested project name
 * {@code openmeteo}. The external service and API remain named Open-Meteo.</p>
 *
 * <p>The package contains:</p>
 * <ul>
 *   <li>a typed plugin configuration adapter;</li>
 *   <li>a JDK HTTP client implementation;</li>
 *   <li>Jackson response records;</li>
 *   <li>an immutable daily observation record;</li>
 *   <li>complete WMO weather-code descriptions;</li>
 *   <li>a plugin facade ready for future registry integration.</li>
 * </ul>
 */
package com.towermarsh.opendata.plugin.openmeteo;
