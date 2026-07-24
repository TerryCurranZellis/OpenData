/*
 *  Filename: package-info.java
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
