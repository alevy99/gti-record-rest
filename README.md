# GTI Record REST

A multi-module Java application built with **Spring Boot 3** providing a REST backend, desktop client, and shared core/model libraries for the GTI Record system. Developed as part of an main course project at Galway Technological Institute.

---

## Project Structure

```
gti-record-rest/
├── model/          # Shared data model / domain classes
├── core/           # Shared business logic and utilities
├── backend/        # Spring Boot REST API server
├── desktop-app/    # Desktop client application
├── android-app/    # Android client (currently excluded from build)
├── db/             # Database scripts / migrations
├── src/            # Root-level resources
├── Dockerfile      # Docker image definition for the backend
├── Procfile        # Process definition (e.g. for Heroku / cloud deployment)
└── pom.xml         # Root Maven POM (multi-module)
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.4 |
| Build tool | Maven 3.9.4 |
| Logging | Apache Log4j 2.24.3 (via SLF4J bridge) |
| Database | MySQL (configurable via environment variables) |
| Containerization | Docker (in development) |

---

## Prerequisites

- Java 21+
- Maven 3.9.4+
- MySQL database (for the backend)
- Docker (optional, for containerised deployment)

---

## Building

The project uses Maven multi-module profiles. You can build specific parts of the system using the provided profiles.

**Build everything (default):**
```bash
mvn clean install
```

**Build backend only:**
```bash
mvn clean install -P backend
```

**Build desktop app only:**
```bash
mvn clean install -P desktop
```

**Build all modules explicitly:**
```bash
mvn clean install -P all
```

---

## Modules

### `model`
Contains shared domain/entity classes used across the backend and client applications.

### `core`
Contains shared business logic, service interfaces, and utility classes.

### `backend`
Spring Boot application exposing the REST API. Entry point: `ie.gti.asdl.rey.gtirecord.backend.GtiRecordRestServer`.

### `desktop-app`
Desktop client application consuming the REST API.

### `android-app`
Android client (currently excluded from the Maven build — work in progress).

---

## Running the Backend

After building, run the backend JAR directly:

```bash
java -jar backend/target/gti-record-rest-1.0.jar
```

The server will start on **port 8080** by default.

---

## REST API

The backend exposes a RESTful API on `http://localhost:8080`.

> **Note:** The REST API is only active when the `web` Spring profile is enabled.  
> Start the server with: `java -jar gti-record-backend-1.0.jar --spring.profiles.active=web`

---

### Users

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `GET` | `/users/id/{id}` | Get user by numeric ID | `User` object or `404` |
| `GET` | `/users/name/{username}` | Get user by username | `User` object or `404` |

**Example:**
```
GET http://localhost:8080/users/id/1
GET http://localhost:8080/users/name/john
```

---

### Students

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `GET` | `/students` | Get all students | `List<Student>` |

---

### Teachers

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `GET` | `/teachers` | Get all teachers | `List<Teacher>` |

---

### Groups

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `GET` | `/groups` | Get all groups | `List<Group>` |

---

### Modules

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `GET` | `/modules` | Get all modules | `List<Module>` |
| `GET` | `/modules/teacher/{teacherPersonId}` | Get modules assigned to a specific teacher | `List<Module>` |

**Example:**
```
GET http://localhost:8080/modules/teacher/5
```

## Docker *(in development)*

> ⚠️ **Note:** Docker support is an optional, experimental feature and has not been fully tested. The `Dockerfile` is provided as a starting point for containerised deployment but is not considered production-ready at this stage.

The backend is intended to run inside an OpenJDK 21 slim container, with MySQL connection details passed via environment variables at runtime:

| Variable | Description |
|---|---|
| `MYSQL_HOST` | MySQL server hostname |
| `MYSQL_PORT` | MySQL server port |
| `MYSQL_DB` | Database name |
| `MYSQL_USER` | Database username |
| `MYSQL_PASSWORD` | Database password |

Full Docker build and run instructions will be added once this functionality is stabilised.

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## License

This project is developed as part of an academic programme at ATU Galway (Galway Technological Institute). See `LICENSE` for details (if applicable).
