package com.towermarsh.opendata.config.model;

/**
 * Strategy used to obtain the dataset content.
 */
public enum DownloadStrategyType {
    DIRECT_HTTP,
    AUTHENTICATED_API,
    HTML_LINK_DISCOVERY,
    HTML_TABLE,
    BROWSER_AUTOMATION
}
