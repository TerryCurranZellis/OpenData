/* 
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0 
 * 
 */

package com.towermarsh.opendata.plugin.octopus.load;

public record OctopusPersistenceResult(
        long inserted, 
        long updated, 
        long skipped) {
}
