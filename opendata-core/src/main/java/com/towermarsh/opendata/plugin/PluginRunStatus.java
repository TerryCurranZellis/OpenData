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
     * plugin is running
     */
    RUNNING("running"),

    /**
     * plugin has succeeded
     */
    SUCCESS("success"),

    /**
     * plugin is doing a dry run
     */
    DRY_RUN("dry run"),

    /**
     * plugin has failed
     */
    FAILED("failed"),

    /**
     * plugin was cancelled
     */
    CANCELLED("cancelled");
    
    /**
     * friendly status name
     */
    private final String displayName;

    PluginRunStatus(final String displayName) {
        this.displayName = displayName;
    }
     /**
     * Returns the human-readable status description.
     *
     * @return display name
     */
    public String displayName() {
        return displayName;
    }
}
