# ADR-001: Use Local MySQL for Primary Database

## Status
Accepted

## Date
2026-07-23

## Context
The application initially utilized an in-memory H2 database with automatic data seeding (`DataLoader`). 
However, for a real-world coffee shop management system, we need a persistent database that maintains data between application restarts and allows for external manual queries (e.g., via MySQL Workbench).

Key requirements:
- Relational data model for users, menu items, orders, and customizations.
- Persistence across application restarts.
- Ability to easily inspect and modify data via standard SQL tools.

## Decision
Migrate the primary database to a local MySQL instance (`coffee_shop`) and replace Spring Boot's automatic data seeding (`DataLoader`) with standard SQL scripts (`schema.sql` and `data.sql`).

## Alternatives Considered

### H2 In-Memory
- **Pros**: Zero setup, fast, embedded.
- **Cons**: Data is wiped on every restart. Unsuitable for production or long-term development testing.
- **Rejected**: Fails the persistence requirement.

### SQLite
- **Pros**: File-based persistence, zero server setup.
- **Cons**: Limited concurrency for a web backend.
- **Rejected**: MySQL is the standard taught in the course and handles enterprise web concurrency better.

## Consequences
- Requires developers/users to have MySQL installed locally with root access.
- We must manually run `schema.sql` and `data.sql` to initialize the database instead of relying on Spring Boot's `CommandLineRunner`.
- `application.yml` is permanently pointed to `jdbc:mysql://localhost:3306/coffee_shop`.
