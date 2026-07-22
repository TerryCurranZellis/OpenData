package com.towermarsh.opendata.config.model;

import java.util.Objects;

/**
 * Reference to a secret held outside the plugin definition.
 *
 * @param name credential name
 * @param authenticationType authentication mechanism
 * @param provider credential provider identifier
 * @param secretReference provider-specific secret reference
 * @param location request location
 * @param parameterName header, query parameter or cookie name
 */
public record CredentialReference(
        String name,
        AuthenticationType authenticationType,
        String provider,
        String secretReference,
        CredentialLocation location,
        String parameterName) {

    public CredentialReference {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(authenticationType, "authenticationType");
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(secretReference, "secretReference");
        Objects.requireNonNull(location, "location");
        parameterName = parameterName == null ? "" : parameterName;
    }
}
