# PARKING-LOT-RESERVATION
A Spring Boot backend application for managing parking floors, slots, vehicle rates, and reservations with availability checks, conflict handling, and fee calculation.

---

## Project Overview

This project implements a Parking Lot Reservation system featuring:

- CRUD operations for Parking Floors, Slots, and Vehicle Rates
- Reservation management with time overlap checking and total fee calculation
- Pagination and sorting support for available parking slots
- Validation for vehicle number formats and reservation durations
- Optimistic locking for concurrency control
- Global exception handling for meaningful error responses
- Unit tests ensuring high code coverage of core business logic

---

## Technologies Used

- Java 17
- Spring Boot 3.5.4
- Spring Data JPA with Hibernate
- MySQL database
- Maven for build and dependency management
- JUnit 5 and Mockito for testing
- Lombok for boilerplate reduction
- Swagger (OpenAPI) for API documentation
- HikariCP connection pool

---

## Prerequisites

- Java 17 JDK installed
- Maven installed
- Spring Tool Suite Installed
- lombok JAR Installed
- MySQL database running locally or accessible remotely


---

## Setup Instructions

1. **Clone the repository**                                                                                              git clone https://github.com/Basil-KB/PARKING-LOT-RESERVATION.git
cd PARKING-LOT-RESERVATION
2. **Create Database**
         Set up a MySQL database named `parking_lot`:
3. **Configure application.properties**
        Modify `src/main/resources/application.properties` as per your database credentials.

4.**Build the Project**
5. **Run the Application**
      The application will start at: `http://localhost:8080`
      ## Accessing the Application
The application runs at `http://localhost:8080`. However, the root URL (`/`) does **not** serve a static homepage or web interface by default since this is a backend REST API application.

---
## API Documentation & Explore the API

Swagger UI documentation is available at:http://localhost:8080/swagger-ui/index.html
Use it to explore and test API endpoints interactively.

---
## API Usage

- Floor APIs  
- Create: `POST /api/floors`  
- Retrieve: `GET /api/floors/{id}`  
- Update: `PUT /api/floors/{id}`

- Slot APIs  
- Create: `POST /api/slots?floorId={floorId}`  
- Retrieve: `GET /api/slots/{id}`  
- Update: `PUT /api/slots/{id}`

- Vehicle Rate APIs  
- Create: `POST /api/vehicle-rates`  
- Update: `PUT /api/vehicle-rates/{id}`

- Reservation APIs  
- Reserve slot: `POST /api/reserve`  
- Get reservation: `GET /api/reservations/{id}`  
- Cancel reservation: `DELETE /api/reservations/{id}`  
- Reschedule reservation: `PUT /api/reservations/{id}/`

- Availability API  
- Get available slots: `GET /api/availability/`

---

## Testing

Run unit and integration tests

