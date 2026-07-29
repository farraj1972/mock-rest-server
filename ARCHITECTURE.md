# Architecture

## High-Level Components

-   Controllers
-   Endpoint Registry
-   Request Processing
-   Forwarding Engine
-   Administration API
-   Configuration Persistence

## Configuration Flow

    config/endpoints.json
            │
    EndpointRegistry
            │
    Validation
            │
    Registry Index
            │
    Request Processing

## Runtime Flow

Client → Controller → EndpointRegistry

If forwarding is disabled: - Mock response

If forwarding is enabled: - Transparent proxy

## Design Principles

-   Configuration over code
-   Stable endpoint identifiers
-   Incremental evolution
-   Small, isolated foundations
-   Production-first design
