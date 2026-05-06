# Train Ticket Booking System

A console-based Java application for managing train schedules, booking tickets, finding routes between stations, and performing administrative operations — with email notifications for booking confirmations and delay alerts.

Built as part of the Siemens Summer Practice Program 2026 technical assessment.

---

## Features

| Area | Functionality |
|------|--------------|
| **Booking** | Book one or multiple tickets on any scheduled train, with overbooking prevention |
| **Email Notifications** | Confirmation email on booking; delay notification email when an admin reports a delay |
| **Route Finding** | Find departure/arrival options between any two stations — direct or with changeovers |
| **Admin — Routes** | Add, modify, or remove routes and their station sequences |
| **Admin — Trains** | Add, modify, or remove trains (name + seat capacity) |
| **Admin — Schedules** | Create or remove schedules (train + route + departure time) |
| **Admin — Bookings** | View all bookings made for any train |
| **Admin — Delays** | Report a delay for a train; all booked passengers are notified via email |

---

## Tech Stack

- **Java 11** (OpenJDK)
- **Spring Boot 2.7** — dependency injection, autoconfiguration, `CommandLineRunner`
- **Spring Data JPA** — repository layer with Hibernate
- **H2 Database** — in-memory, zero-config; swappable to any RDBMS
- **Spring Mail** — `JavaMailSender` for SMTP email
- **Maven Wrapper** — no Maven installation needed

---

## Project Structure

```
src/main/java/com/trainsystem/
├── TrainTicketApplication.java        Entry point
├── cli/
│   ├── AppRunner.java                 Main menu (CommandLineRunner)
│   ├── CustomerMenu.java             Booking & route finding
│   └── AdminMenu.java                Admin CRUD & delay reporting
├── model/
│   ├── Station.java
│   ├── Route.java
│   ├── RouteStop.java                 Ordered station within a route
│   ├── Train.java
│   ├── Schedule.java                  Train + Route + departure time
│   ├── Booking.java
│   └── enums/
│       └── TrainStatus.java           ON_TIME, DELAYED, CANCELLED
├── repository/
│   ├── StationRepository.java
│   ├── RouteRepository.java
│   ├── TrainRepository.java
│   ├── ScheduleRepository.java
│   └── BookingRepository.java
├── service/
│   ├── BookingService.java            Overbooking checks + email trigger
│   ├── RouteFinderService.java        BFS graph traversal for connections
│   ├── TrainService.java              CRUD + delay reporting
│   ├── ScheduleService.java           CRUD for schedules
│   ├── RouteService.java              CRUD for routes & stations
│   └── NotificationService.java       Email sending (graceful failures)
└── exception/
    ├── NoRouteFoundException.java
    ├── OverbookingException.java
    └── EntityNotFoundException.java
```

---

## How to Build & Run

### Prerequisites
- **Java 11+** installed and `JAVA_HOME` environment variable set

### Run the application
```bash
# Windows
set JAVA_HOME=C:\path\to\jdk-11
mvnw.cmd spring-boot:run

# Linux / macOS
export JAVA_HOME=/path/to/jdk-11
./mvnw spring-boot:run
```

The application starts with preloaded seed data (6 Romanian cities, 3 routes, 3 trains, 3 schedules) so you can test immediately.

### Build only (no run)
```bash
mvnw.cmd compile
```

---

## Email Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

For Gmail, generate an App Password at https://myaccount.google.com/apppasswords.

> **Note:** The application runs perfectly without valid SMTP credentials. Email send failures are logged as warnings but do not interrupt any operation.

---

## Preloaded Seed Data

| Stations | Routes | Trains |
|----------|--------|--------|
| Bucharest | Bucharest - Brasov Express | IR 1581 (200 seats) |
| Brasov | Brasov - Cluj via Sibiu | IR 1732 (150 seats) |
| Cluj-Napoca | Bucharest - Iasi Direct | IR 1990 (180 seats) |
| Timisoara | | |
| Sibiu | | |
| Iasi | | |

**Schedules:** Each train runs its assigned route on `2026-06-15`.

---

## Usage Examples (Input & Output)

### Main Menu
```
╔═══════════════════════════════════════╗
║     TRAIN TICKET BOOKING SYSTEM       ║
╚═══════════════════════════════════════╝

═══════════════════════════════════
          MAIN MENU
═══════════════════════════════════
  1. Customer
  2. Administrator
  0. Exit
───────────────────────────────────
Choice:
```

---

### a) Booking Tickets

**Input:**
```
Choice: 1

═══════════════════════════════════
         CUSTOMER MENU
═══════════════════════════════════
  1. Book ticket(s)
  2. Find routes between stations
  0. Back to main menu
───────────────────────────────────
Choice: 1

Available schedules:
  [1] IR 1581 | Bucharest - Brasov Express | Departs: 2026-06-15T08:30 | Available seats: 200
  [2] IR 1732 | Brasov - Cluj via Sibiu | Departs: 2026-06-15T12:00 | Available seats: 150
  [3] IR 1990 | Bucharest - Iasi Direct | Departs: 2026-06-15T06:45 | Available seats: 180

Schedule ID: 1
Passenger name: Andrei Popescu
Email address: andrei@example.com
Number of seats: 2
```

**Output:**
```
✓ Booking confirmed!
  Booking #1 | Andrei Popescu (andrei@example.com) | 2 seat(s) on Schedule #1
  A confirmation email has been sent to andrei@example.com
```

**Overbooking prevention — Output when not enough seats:**
```
✗ Cannot book 300 seat(s) — only 198 available.
```

---

### b) Finding Routes Between Stations

**Direct route — Input:**
```
Choice: 2

Origin station: Bucharest
Destination station: Brasov
```

**Output:**
```
Found 1 connection(s):

Option 1: Direct: Bucharest -> Brasov [Bucharest - Brasov Express] (IR 1581 at 2026-06-15T08:30)
```

**Changeover route — Input:**
```
Origin station: Bucharest
Destination station: Cluj-Napoca
```

**Output:**
```
Found 1 connection(s):

Option 1: Changeover (2 legs):
  Leg 1: Bucharest -> Brasov [Bucharest - Brasov Express] (IR 1581 at 2026-06-15T08:30)
  Leg 2: Brasov -> Cluj-Napoca [Brasov - Cluj via Sibiu] (IR 1732 at 2026-06-15T12:00)
```

**No route found — Input:**
```
Origin station: Iasi
Destination station: Timisoara
```

**Output:**
```
✗ No route found between 'Iasi' and 'Timisoara'.
```

---

### c) Administrator Operations

#### Managing Routes

**Adding a new route:**
```
Choice: 2 (Admin) → 2 (Manage routes) → 2 (Add route)

Route name: Sibiu - Timisoara Express
  [1] Bucharest
  [2] Brasov
  [3] Cluj-Napoca
  [4] Timisoara
  [5] Sibiu
  [6] Iasi
Enter station IDs in order (comma-separated): 5, 4

✓ Route created: Sibiu - Timisoara Express (id=4)
```

#### Managing Trains

**Adding a new train:**
```
Choice: 3 (Manage trains) → 2 (Add train)

Train name: IR 2050
Total seats: 220

✓ Train created: [4] IR 2050 (220 seats, ON_TIME)
```

**Modifying a train:**
```
Choice: 3 (Modify train)

  [1] IR 1581 (200 seats, ON_TIME)
  [2] IR 1732 (150 seats, ON_TIME)
  [3] IR 1990 (180 seats, ON_TIME)
  [4] IR 2050 (220 seats, ON_TIME)
Train ID to modify: 4
New name: IR 2050 Premium
New total seats: 250

✓ Train updated: IR 2050 Premium (250 seats, ON_TIME)
```

#### Viewing Bookings

```
Choice: 5 (View bookings for a train)

  [1] IR 1581 (200 seats, ON_TIME)
Train ID: 1

Bookings:
  Booking #1 | Andrei Popescu (andrei@example.com) | 2 seat(s) on Schedule #1
```

#### Reporting a Delay

```
Choice: 6 (Report train delay)

  [1] IR 1581 (200 seats, ON_TIME)
Train ID: 1
Delay in minutes: 45

✓ Delay recorded. All affected passengers have been notified.
```

All passengers with bookings on that train receive an email:
```
Subject: Train Delay Notice — IR 1581

Dear Andrei Popescu,

We regret to inform you that your train has been delayed.

Booking ID: 1
Train: IR 1581
Route: Bucharest - Brasov Express
Original Departure: 2026-06-15T08:30
Delay: 45 minute(s)

We apologize for the inconvenience.
```

---

## Architecture Notes

- **Layered design**: CLI → Service → Repository → Database. Each layer has a single responsibility and can be replaced independently.
- **Route finding** uses BFS graph traversal across the station adjacency graph derived from all routes. It finds both direct and multi-leg (changeover) journeys.
- **Overbooking prevention** is enforced at the service layer by querying the sum of booked seats before accepting a new booking.
- **Email failures are non-blocking** — logged as warnings so the application remains fully functional without SMTP.
- **H2 in-memory database** with `data.sql` seed data. To switch to a persistent database (e.g., PostgreSQL), update the datasource properties in `application.properties`.

---

## License

This project was created for the Siemens Summer Practice Program 2026 technical assessment.
