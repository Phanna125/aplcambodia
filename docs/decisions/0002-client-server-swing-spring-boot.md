# ADR-002: Client-Server Architecture (Swing GUI to Spring Boot REST)

## Status
Accepted

## Date
2026-07-23

## Context
The project contains both a backend (`backend/` Spring Boot) and a frontend desktop app (`desktop-app/` Java Swing). 
Initially, the Swing app contained hardcoded mock data and did not communicate with the backend. 
To function as a cohesive system, the UI needs to reflect live database state, manage real orders, and enforce role-based access control (Admin, Cashier, Barista, Customer).

## Decision
Establish a strict Client-Server architecture. The Swing desktop app will act solely as a thin client, making HTTP REST calls to the Spring Boot backend using a custom `ApiClient` powered by standard `java.net.HttpURLConnection` (and `java.net.URI`). 

## Alternatives Considered

### Direct Database Connection (2-Tier Architecture)
- **Pros**: Simpler to implement in Swing (just use JDBC directly).
- **Cons**: Massive security risk. Exposes database credentials to the client. No central business logic. Cannot easily add web/mobile clients later.
- **Rejected**: A 3-tier architecture with a REST API is the modern standard.

### Third-Party HTTP Libraries (OkHttp, Apache HttpClient)
- **Pros**: Easier to use than `HttpURLConnection`.
- **Cons**: Adds extra dependencies to the desktop app.
- **Rejected**: We opted to keep the desktop app lightweight, relying only on `Gson` for JSON parsing and built-in Java libraries for HTTP.

## Consequences
- The Swing application cannot function offline or without the backend running (hence the need for `run-all.bat`).
- All state changes (creating orders, adding menu items) must go through the network, which may introduce slight latency but ensures data consistency.
- Authentication is handled via JWT tokens passed in the `Authorization: Bearer <token>` header by the `ApiClient`.
