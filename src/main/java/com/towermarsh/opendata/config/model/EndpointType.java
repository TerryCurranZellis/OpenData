/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config.model;

/**
 * Purpose and retrieval behaviour of a configured endpoint.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public enum EndpointType {

    /**
     * api results
     */
    API,

    /**
     * file to download
     */
    FILE,

    /**
     * Landing page
     */
    LANDING_PAGE,

    /**
     * html tabke
     */
    HTML_TABLE,

    /**
     * web page meta data
     */
    METADATA,

    /**
     * Authentication page 
     */
    AUTHENTICATION
}
