# ADR-0011: Use the Java HTTP client for dataset downloads

- Status: Accepted
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

The framework must download public datasets over HTTP and HTTPS. Java 17 includes a capable asynchronous and synchronous HTTP client, so an additional general-purpose HTTP library is not automatically required.

## Decision

Use `java.net.http.HttpClient` as the default HTTP transport for dataset acquisition. Wrap it behind framework download interfaces so a plugin can replace or specialise transport behaviour when required.

## Consequences

### Positive

- No additional HTTP client dependency is required.
- Supports HTTP/2, redirects, timeouts, synchronous and asynchronous calls.
- Can be injected and mocked through a wrapper interface.
- Fits the preference for standard Java APIs.

### Negative or limiting

- Advanced retry, connection-management, or multipart features may need framework code or a specialised library.
- Direct use throughout plugins would make testing harder, so abstraction discipline is required.

## Alternatives considered

### Apache HttpClient

Not selected initially because the JDK client meets the known requirements.

### Plugin-specific direct URL access

Rejected because it would duplicate timeout, header, error-handling, and logging behaviour.

## Implementation notes

Central download configuration should cover connection timeout, request timeout, redirect policy, user agent, retry policy, target directory, and overwrite behaviour. Secrets must never be logged.
