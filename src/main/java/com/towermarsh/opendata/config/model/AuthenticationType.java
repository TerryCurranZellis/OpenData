package com.towermarsh.opendata.config.model;

/**
 * Authentication mechanisms supported by endpoint definitions.
 */
public enum AuthenticationType {
    NONE,
    API_KEY,
    BASIC,
    BEARER_TOKEN,
    OAUTH2_CLIENT_CREDENTIALS,
    FORM_LOGIN,
    COOKIE
}
