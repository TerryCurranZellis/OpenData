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
     *
     */
    HEADER,

    /**
     *
     */
    QUERY_PARAMETER,

    /**
     *
     */
    COOKIE,

    /**
     *
     */
    REQUEST_BODY,

    /**
     *
     */
    NONE
}
