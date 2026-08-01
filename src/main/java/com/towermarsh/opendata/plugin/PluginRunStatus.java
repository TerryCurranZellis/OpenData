/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

/** Database and process status for one plugin task.  *
* @author Terry Curran
* @version 1.0.0
*/
public enum PluginRunStatus {

    /**
     *
     */
    RUNNING,

    /**
     *
     */
    SUCCESS,

    /**
     *
     */
    DRY_RUN,

    /**
     *
     */
    FAILED,

    /**
     *
     */
    CANCELLED
}
