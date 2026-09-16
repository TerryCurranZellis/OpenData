/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config.model;

/**
 * Strategy used to obtain the dataset content.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public enum DownloadStrategyType {

    /**
     * direct http
     */
    DIRECT_HTTP,

    /**
     * Authenticate api call
     */
    AUTHENTICATED_API,

    /**
     * html link
     */
    HTML_LINK_DISCOVERY,

    /**
     * html table
     */
    HTML_TABLE,

    /**
     * browser automation
     */
    BROWSER_AUTOMATION
}
