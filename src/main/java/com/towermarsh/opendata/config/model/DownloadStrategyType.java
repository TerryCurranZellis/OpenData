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
 * @version 17 July 2026
 */
public enum DownloadStrategyType {

    /**
     *
     */
    DIRECT_HTTP,

    /**
     *
     */
    AUTHENTICATED_API,

    /**
     *
     */
    HTML_LINK_DISCOVERY,

    /**
     *
     */
    HTML_TABLE,

    /**
     *
     */
    BROWSER_AUTOMATION
}
