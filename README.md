# Vibego Logistics Backend

A Spring Boot backend for a logistics platform managing drivers, vehicles, requests, notifications, and orders.

## Technologies
- Java 21 (compatible with Spring Boot)
- Spring Boot 3.3.4 (specified 4.0.2 not yet available)
- H2 Database (in-memory)
- Spring Data JPA
- Spring HATEOAS for RESTful links
- Maven

## Key Features
- Create package delivery requests (async processing)
- Match with vehicles/drivers
- Driver notifications (poll and respond)
- Order creation on accept
- Status updates with callback simulation
- Full REST APIs with HATEOAS
- Entities: Request, Order, Driver, Vehicle, Notification, VehicleDriver

## Endpoints (examples)
- POST /api/requests - Create request
- GET /api/requests/{id}/status - Get status (callback simulation)
- GET /api/notifications/driver/{driverId} - Poll notifications
- POST /api/notifications/{id}/respond?accept=true&driverId=xx - Respond
- POST /api/orders/{id}/cancel - Cancel order (updates request)
- GET /api/drivers, /api/vehicles - CRUD basics

## Running
mvn spring-boot:run (dependencies needed)

Sample data seeded on startup.

## New: User Management
- User table/entity for auth (register/login endpoints)
- Spring Security basic setup (permit public endpoints)
- Driver now includes email and userId link
- POST /api/users/register , /api/users/login
