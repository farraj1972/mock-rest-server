# CURRENT_ARCHITECTURE.md

## Mock REST Server

### Current Architecture & Technical Roadmap

**Version:** 1.0
**Status:** Approved Baseline
**Branch:** develop

---

# 1. Vision

The project started as a lightweight configurable REST Mock Server.

Its evolution transforms it into a lightweight **API Virtualization Platform**, capable of dynamically operating as:

* Mock Server
* Transparent Proxy
* API Gateway (lightweight)
* API Virtualization Platform

without requiring any changes in REST clients.

The original mock functionality remains the default behavior.

---

# 2. Objectives

The project must provide a lightweight, configuration-driven platform capable of:

* serving configurable mock responses;
* transparently forwarding requests to remote REST services;
* dynamically switching between Mock and Proxy modes;
* logging every invocation;
* allowing runtime administration without restarting the server.

The project intentionally avoids becoming a full API Gateway.

---

# 3. Design Principles

The architecture follows the following principles.

## 3.1 Configuration Driven

Business behavior is completely defined by configuration.

No endpoint-specific Java code should exist.

---

## 3.2 Stateless

The server must remain stateless.

Runtime changes affect only the in-memory configuration unless persistence is explicitly introduced in the future.

---

## 3.3 Single Responsibility

Each component has exactly one responsibility.

Controllers never implement business logic.

---

## 3.4 Composition over Complexity

New features are introduced by adding services to the processing pipeline rather than modifying existing components.

---

## 3.5 Evolution without Disruption

New capabilities must not require modifications to REST clients.

The same endpoint may behave as:

* Mock
* Proxy

depending exclusively on configuration.

---

# 4. High Level Architecture

```
                 HTTP Request
                       │
                       ▼
              MockController
                       │
                       ▼
              RequestProcessor
                       │
        ┌──────────────┴──────────────┐
        │                             │
        ▼                             ▼
 EndpointRegistry             Admin Services
        │
        ▼
 EndpointDefinition
        │
        ▼
 Forward Enabled ?
        │
 ┌──────┴─────────┐
 │                │
 ▼                ▼
MockService   ProxyService
        │
        ▼
ConsoleLogger
        │
        ▼
 HTTP Response
```

The controller never decides whether a request is mocked or forwarded.

This decision belongs exclusively to the RequestProcessor.

---

# 5. Core Components

## MockController

Responsibilities:

* receive HTTP requests;
* delegate processing;
* return the produced response.

It contains no business logic.

---

## RequestProcessor

Central orchestration component.

Responsibilities:

* locate endpoint configuration;
* decide processing mode;
* invoke MockService or ProxyService;
* execute processing pipeline;
* guarantee common logging behaviour.

All future processing stages are coordinated here.

---

## EndpointRegistry

Responsibilities:

* load endpoint configuration;
* validate configuration;
* maintain in-memory index;
* provide endpoint lookup.

No HTTP logic exists in this component.

---

## EndpointDefinition

Represents a configured endpoint.

Future internal structure:

```
EndpointDefinition

    Request

    Response

    Forward
```

This avoids a monolithic class as new capabilities are introduced.

---

## MockService

Produces responses locally.

Receives:

* EndpointDefinition

Returns:

* ResponseEntity

No routing logic exists here.

---

## ProxyService

Responsible for transparent forwarding.

Responsibilities:

* recreate incoming request;
* forward request;
* receive remote response;
* preserve HTTP semantics;
* return response unchanged unless transformation is configured.

---

## ConsoleLogger

Logs every request independently of execution mode.

The logger must remain common for:

* Mock
* Proxy

Future versions should also log:

* processing mode;
* response status;
* execution time.

---

# 6. Endpoint Model

Current endpoint configuration will evolve towards:

```json
{
  "request": {
    "method": "GET",
    "path": "/customers"
  },

  "response": {
    "status": 200,
    "headers": {},
    "body": {}
  },

  "forward": {
    "enabled": false,
    "url": "https://remote.service/api"
  }
}
```

The forward section controls transparent proxy behaviour.

---

# 7. Processing Flow

```
Receive Request
       │
       ▼
Resolve Endpoint
       │
       ▼
Endpoint Exists?
       │
 ┌─────┴─────┐
 │           │
 ▼           ▼
404       Forward Enabled?
               │
       ┌───────┴────────┐
       │                │
       ▼                ▼
MockService       ProxyService
       │                │
       └───────┬────────┘
               ▼
        ConsoleLogger
               ▼
        HTTP Response
```

---

# 8. Runtime Administration

The application will expose a dedicated administration API.

Administration endpoints are completely separated from business endpoints.

```
/api/**

Business Requests

/admin/**

Administration
```

Administration operations include:

* enable forward;
* disable forward;
* inspect endpoint configuration;
* runtime configuration changes.

Changes affect only memory.

Persistence may be introduced later.

---

# 9. Future Processing Pipeline

The architecture intentionally allows insertion of additional processing stages.

```
Resolve Endpoint

↓

Forward Decision

↓

Authentication Filter

↓

Request Transformation

↓

Proxy

↓

Response Transformation

↓

Logging

↓

HTTP Response
```

Every stage performs exactly one responsibility.

---

# 10. Non-Goals

The project intentionally avoids:

* Enterprise API Gateway features;
* Authentication server;
* Service discovery;
* Distributed configuration;
* Cluster management;
* Rate limiting;
* OAuth server;
* Load balancing.

Those capabilities are outside the project scope.

---

# 11. Technical Roadmap

## Foundation 1

Architecture consolidation.

* RequestProcessor
* MockService
* ProxyService (stub)

---

## Foundation 2

Endpoint model evolution.

* Forward configuration
* Configuration model update

---

## Foundation 3

Transparent Proxy.

* HTTP forwarding
* Preserve headers
* Preserve status
* Preserve body

---

## Foundation 4

Administration API.

* Enable/Disable Forward
* Runtime updates

---

## Foundation 5

Runtime configuration management.

* In-memory updates
* Endpoint inspection

---

## Foundation 6

Processing pipeline.

* Request interceptors
* Response interceptors
* Header manipulation

---

## Foundation 7

Record / Replay.

* Capture remote responses
* Replay recorded responses

---

# 12. Architectural Rules

The following rules are mandatory.

1. Controllers contain no business logic.

2. RequestProcessor is the single orchestration component.

3. Services never invoke each other cyclically.

4. EndpointRegistry is the single source of endpoint configuration.

5. MockService never performs HTTP forwarding.

6. ProxyService never generates mock responses.

7. ConsoleLogger logs every request regardless of execution mode.

8. New features should be introduced by extending the processing pipeline instead of modifying existing components.

---

# 13. Long-Term Vision

The project should evolve into a lightweight API Virtualization platform capable of operating transparently between REST clients and backend services.

The architecture prioritises simplicity, extensibility and maintainability over feature quantity.

Every architectural decision should preserve these principles.
