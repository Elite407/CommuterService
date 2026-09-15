# RidePlatform (formerly CommuterService)

RidePlatform is a backend architecture designed for a ride-hailing service. It provides the core infrastructure for passenger and partner management, trip coordination, dynamic pricing, and digital wallet operations.

## Architecture

The application is structured as a Spring Boot REST API utilizing a Package-by-Feature architecture. Key components include:

- **Passenger Domain:** Manages passenger registration and profiles.
- **Partner Domain:** Handles driver onboarding, vehicle details, and license verification.
- **Trip Domain:** Coordinates ride requests, trip lifecycle management, and driver assignment.
- **Pricing Engine:** Calculates fares using the Haversine formula based on distance, time, and dynamic surge multipliers.
- **Wallet Domain:** Manages partner earnings and financial transactions with optimistic locking to prevent race conditions.

## Technology Stack

- Java 17
- Spring Boot 3.2.x
  - Spring Web
  - Spring Data JPA
- PostgreSQL
- Swagger / OpenAPI (springdoc-openapi)
- jBCrypt (Standalone password hashing)

## Database Schema

The persistence layer uses a normalized relational schema with UUID primary keys and automated audit timestamps. The data model includes entities for passengers, partners, vehicles, trips, wallets, fare rules, and surge pricing zones.

## Setup Requirements

- Java 17 Development Kit (JDK)
- Apache Maven
- PostgreSQL 15+

A local database configuration template is provided in `config.properties.example`. Ensure an `.env` file is present in the root directory for runtime credential resolution.
