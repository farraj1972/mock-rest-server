# Mock REST Server

> A configurable REST API virtualization platform for mocking,
> simulation and transparent proxying of HTTP services.

## Overview

Mock REST Server is a Spring Boot application that allows REST endpoints
to be defined entirely through configuration. It can behave as a mock
server, a transparent proxy or a hybrid virtualization platform.

### Current Features

-   Configuration-driven endpoints
-   Runtime Administration API
-   Transparent HTTP forwarding
-   Persistent configuration
-   External JSON configuration
-   Spring Boot 3 / Jackson

## Project Goals

-   Accelerate API development
-   Simplify integration testing
-   Support service virtualization
-   Provide runtime configuration without redeployment

## Foundations

  Foundation   Description                  Status
------------ ---------------------------- --------
  1            Core Request Processing      ✅
  2            Forward Configuration        ✅
  3            Transparent Proxy            ✅
  4            Runtime Administration API   ✅
  5            Persistent Configuration     ✅

## Quick Start

``` bash
mvn spring-boot:run
```

or

``` bash
java -jar mock-rest-server.jar
```

On first startup the application creates:

``` text
config/endpoints.json
```

from the bundled default configuration.

## Documentation

-   ARCHITECTURE.md
-   CHANGELOG.md
-   ROADMAP.md
-   CONTRIBUTING.md
