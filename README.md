# Mock REST Server

> A configurable REST API virtualization platform for mocking, simulation and transparent proxying of HTTP services.

---

# Overview

Mock REST Server is a lightweight Spring Boot application that allows REST endpoints to be defined entirely through configuration, without requiring application code changes.

It can operate as:

- REST API mock server
- HTTP proxy
- Hybrid mock/proxy server
- Runtime configurable virtualization platform

The application is designed to support API development, integration testing, QA environments and service virtualization.

---

# Key Features

- Configuration-driven endpoints
- Runtime administration API
- Transparent HTTP forwarding
- Persistent configuration
- External configuration files
- JSON request/response support
- Spring Boot 3
- Java 21 compatible (adjust if your project uses another version)

---

# Architecture

```
                +-------------------------+
                |     REST Client         |
                +------------+------------+
                             |
                             v
                 +-----------+-----------+
                 | Mock REST Server      |
                 +-----------+-----------+
                             |
          +------------------+------------------+
          |                                     |
          v                                     v
    Mock Response                      Forward Request
                                              |
                                              v
                                   External REST Service
```

The server decides whether to return a configured response or forward the request according to each endpoint configuration.

---

# Project Structure

```
src
 ├── controller
 ├── service
 ├── model
 ├── forwarding
 ├── registry
 ├── configuration
 └── exception
```

Configuration files

```
config/
    endpoints.json
```

---

# Endpoint Configuration

Endpoints are defined in a single JSON file.

Example:

```json
{
  "id": "customers-get",
  "method": "GET",
  "path": "/api/customers",
  "forward": {
    "enabled": false
  }
}
```

Every endpoint has a stable identifier used by the administration API.

---

# Administration API

Retrieve endpoints

```
GET /admin/endpoints
```

Retrieve endpoint

```
GET /admin/endpoints/{id}
```

Enable forwarding

```
PUT /admin/endpoints/{id}/enable
```

Disable forwarding

```
PUT /admin/endpoints/{id}/disable
```

Update forwarding

```
PUT /admin/endpoints/{id}/forward
```

---

# Configuration Persistence

Runtime changes are automatically persisted.

```
config/
    endpoints.json
```

The application automatically creates the configuration directory on first execution.

---

# Startup

Run

```
mvn spring-boot:run
```

or

```
java -jar mock-rest-server.jar
```

On first execution:

```
config/endpoints.json
```

is automatically created from the default bundled configuration.

---

# Development Methodology

The project follows an incremental engineering approach.

Each Foundation delivers exactly one capability.

Typical workflow:

```
Foundation

↓

One Class

↓

Compile

↓

Functional Test

↓

Commit
```

Completed Foundations remain immutable except for:

- bug fixes
- security issues
- unavoidable architectural dependencies

---

# Current Foundations

| Foundation | Description | Status |
|------------|-------------|--------|
| 1 | Core Request Processing | ✅ |
| 2 | Forward Configuration | ✅ |
| 3 | Transparent Proxy | ✅ |
| 4 | Runtime Administration API | ✅ |
| 5 | Persistent Configuration | ✅ |

---

# Roadmap

Planned next foundations:

- Runtime Configuration Reload
- Response Templates
- Dynamic Variables
- Scenario Engine
- Fault Simulation
- Artificial Latency
- Request Recording
- OpenAPI Import
- Metrics
- Authentication

---

# Design Principles

The project intentionally favors:

- simplicity
- readability
- maintainability
- explicit behaviour
- incremental evolution

over:

- unnecessary abstraction
- speculative architecture
- framework complexity

---

# Technology Stack

- Java
- Spring Boot
- Jackson
- SLF4J
- Maven

---

# License

Internal project.
