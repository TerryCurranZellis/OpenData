/*
 * Filename: WmoWeatherCode.java
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
package com.towermarsh.opendata.plugin.openmeteo.transform.model;

/**
 * Maps WMO weather interpretation codes returned by Open-Meteo.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class WmoWeatherCode {

    /**
     * Prevents instantiation of this utility class.
     */
    private WmoWeatherCode() {
    }

    /**
     * Returns a concise description for a WMO weather code.
     *
     * @param code WMO code
     * @return description
     */
    public static String description(final int code) {
        return switch (code) {
            case 0 ->
                "Clear sky";
            case 1 ->
                "Mainly clear";
            case 2 ->
                "Partly cloudy";
            case 3 ->
                "Overcast";
            case 45 ->
                "Fog";
            case 48 ->
                "Depositing rime fog";
            case 51 ->
                "Light drizzle";
            case 53 ->
                "Moderate drizzle";
            case 55 ->
                "Dense drizzle";
            case 56 ->
                "Light freezing drizzle";
            case 57 ->
                "Dense freezing drizzle";
            case 61 ->
                "Slight rain";
            case 63 ->
                "Moderate rain";
            case 65 ->
                "Heavy rain";
            case 66 ->
                "Light freezing rain";
            case 67 ->
                "Heavy freezing rain";
            case 71 ->
                "Slight snowfall";
            case 73 ->
                "Moderate snowfall";
            case 75 ->
                "Heavy snowfall";
            case 77 ->
                "Snow grains";
            case 80 ->
                "Slight rain showers";
            case 81 ->
                "Moderate rain showers";
            case 82 ->
                "Violent rain showers";
            case 85 ->
                "Slight snow showers";
            case 86 ->
                "Heavy snow showers";
            case 95 ->
                "Thunderstorm";
            case 96 ->
                "Thunderstorm with slight hail";
            case 99 ->
                "Thunderstorm with heavy hail";
            default ->
                "Unknown";
        };
    }
}
