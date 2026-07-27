/*
 * Filename: ExecutionStatus.java
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
package com.towermarsh.opendata.app;

/**
 * Final status reported by one OpenData process invocation.
  *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public enum ExecutionStatus {

    NOT_STARTED(1, "Not started"),
    SUCCESS(0, "Successful"),
    APPLICATION_FAILURE(1, "Application error"),
    COMMAND_LINE_ERROR(2, "Command-line error"),
    CONFIGURATION_ERROR(3, "Configuration error"),
    PLUGIN_FAILURE(4, "One or more plugins failed"),
    DATABASE_FAILURE(5, "Database failure"),
    IO_FAILURE(6, "Input/output failure"),
    INTERRUPTED(130, "Interrupted");

    /**
     * status code number to return to os
     */
    private final int statusCode;
    /**
     * friendly status name
     */
    private final String displayName;

    ExecutionStatus(final int statusCode, final String displayName) {
        this.statusCode = statusCode;
        this.displayName = displayName;
    }

    /**
     * Returns the numeric application status code.
     *
     * @return status code
     */
    public int statusCode() {
        return statusCode;
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
