/* 
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0 
 * 
 */

package com.towermarsh.opendata.plugin.octopus.load;

/**
 * results of loading the octopus data
 * 
 * @patam inserted number of records inserted
 * @param updated number of records updates
 * @param skipped number of records rejected
 * 
 * @author Terry Curran
 * @version 2.0.0
 */
public record OctopusPersistenceResult(
        long inserted, 
        long updated, 
        long skipped) {
}
