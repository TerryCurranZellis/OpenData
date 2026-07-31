/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config.model;

/**
 * Location in which a credential is applied to a request.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public enum CredentialLocation {

    /**
     * its in the header
     */
    HEADER,

    /**
     * its passed as a parameter
     */
    QUERY_PARAMETER,

    /**
     * its a cookie
     */
    COOKIE,

    /**
     * its in the request body
     */
    REQUEST_BODY,

    /**
     * the are none
     */
    NONE
}
