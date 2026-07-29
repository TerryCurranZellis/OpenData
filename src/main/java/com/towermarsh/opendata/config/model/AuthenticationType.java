/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config.model;

/**
 * Authentication mechanisms supported by endpoint definitions.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public enum AuthenticationType {

    /**
     * no authentication
     */
    NONE,

    /**
     * use an API key
     */
    API_KEY,

    /**
     * basic authentication, Username + password
     */
    BASIC,

    /**
     * bearer token
     */
    BEARER_TOKEN,

    /**
     * OAUTH2 credentials
     */
    OAUTH2_CLIENT_CREDENTIALS,

    /**
     * form login
     */
    FORM_LOGIN,

    /**
     * cookie login
     */
    COOKIE
}
