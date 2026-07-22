package com.towermarsh.opendata.config.model;

/**
 * Location in which a credential is applied to a request.
 */
public enum CredentialLocation {
    HEADER,
    QUERY_PARAMETER,
    COOKIE,
    REQUEST_BODY,
    NONE
}
