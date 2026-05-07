# Train Ticket Booking System


A web-based Java Spring Boot application for managing train schedules, booking tickets, finding routes between stations, and performing administrative operations — with email notifications for booking confirmations and delay alerts.

Built as part of the Siemens Summer Practice Program 2026 technical assessment.

---

## Features

| Area | Functionality |
|------|--------------|
| **Web Dashboard** | Modern, dark-themed, glassmorphic UI dashboard to view the state of the railway system. |
| **Booking** | Book one or multiple tickets on any scheduled train via a dropdown form, with overbooking prevention |
| **Email Notifications** | Confirmation email on booking; delay notification email when an admin reports a delay |
| **Route Finding** | Find departure/arrival options between any two stations visually — detects direct or changeovers |
| **Admin — Routes** | Add, modify, or remove routes and their station sequences from a web interface |
| **Admin — Trains** | Add, modify, or remove trains (name + seat capacity) |
| **Admin — Schedules** | Create or remove schedules (train + route + departure time) |
| **Admin — Bookings** | View all bookings made for any train |
| **Admin — Delays** | Report a delay for a train; all booked passengers are notified instantly via email |
| **REST API** | Full JSON API alongside the web UI for programmatic access (Postman, curl, integrations) |
| **Input Validation** | Robust Bean Validation (JSR 380) protecting the service layer from invalid data |

---

## Tech Stack

- **Java 11** (OpenJDK)
- **Spring Boot 2.7** (Spring Web, Spring Data JPA, Spring Mail, Spring Validation)
- **Thymeleaf** — Server-side template engine for rendering the UI
- **H2 Database** — in-memory, zero-config relational database
- **Maven Wrapper** — no Maven installation needed
- **JUnit 5 & Mockito** — Comprehensive unit testing suite with JaCoCo coverage

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

Once running, open your browser and navigate to the web dashboard:
**http://localhost:8080**

The application also exposes a full REST API. You can test it by navigating to:
**http://localhost:8080/api/trains**

The application starts with preloaded seed data (6 Romanian cities, 5 routes, 5 trains, 5 schedules) so you can test immediately.

### Running Tests
To run the automated test suite and generate a JaCoCo coverage report:
```bash
# Windows
mvnw.cmd test

# Linux / macOS
./mvnw test
```
The coverage report will be available at `target/site/jacoco/index.html`.

---

## Email Configuration

By default, the application will attempt to send emails when tickets are booked or delays are reported. If no credentials are provided, it fails gracefully and logs a warning in the console, but the application continues working perfectly.

To enable real emails using a Gmail account:

1. Generate a 16-character App Password at https://myaccount.google.com/apppasswords
2. Open the `run_with_mail.cmd` script in the root directory.
3. Edit the following variables:
   ```cmd
   set SMTP_USERNAME=your_real_email@gmail.com
   set SMTP_PASSWORD=your_16_char_app_password
   ```
4. Run `.\run_with_mail.cmd` to start the application with real email capabilities.

---

## Preloaded Seed Data

| Stations | Routes | Trains |
|----------|--------|--------|
| Bucharest | Bucharest - Brasov Express | IR 1581 (200 seats) |
| Brasov | Brasov - Cluj via Sibiu | IR 1732 (150 seats) |
| Cluj-Napoca | Bucharest - Iasi Direct | IR 1990 (180 seats) |
| Timisoara | Timisoara - Iasi Crossing | IR 2231 (200 seats) |
| Sibiu | Bucharest - Timisoara Direct | IR 2244 (150 seats) |
| Iasi | | |

**Schedules:** Each train runs its assigned route on `2026-06-15`.

---

## Architecture Notes

- **Web MVC Pattern**: Replaced the original CLI interface with standard Spring `@Controller` classes and Thymeleaf HTML templates.
- **REST API Layer**: Parallel `@RestController` classes expose JSON endpoints, reusing the core business services. A `@RestControllerAdvice` globally handles custom exceptions, mapping them to standard HTTP status codes (e.g., 400, 404).
- **Defensive Programming**: Input validation (JSR 380) is strictly enforced at the `@Service` layer via `@Validated`, ensuring data integrity regardless of the entry point (Web UI or REST API).
- **Route finding**: Uses BFS graph traversal across the station adjacency graph derived from all routes. It finds both direct and multi-leg (changeover) journeys.
- **Overbooking prevention**: Enforced at the service layer by querying the sum of booked seats before accepting a new booking.
- **Null Safety**: All service parameters are rigorously protected with `Objects.requireNonNull()`.
- **H2 in-memory database**: Initialized with `data.sql` seed data. To switch to a persistent database (e.g., PostgreSQL), update the datasource properties in `application.properties`.

---

## License

This project was created for the Siemens Summer Practice Program 2026 technical assessment.
