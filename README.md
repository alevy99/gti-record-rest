# GTI Record REST

**GTI Record** is a student record management system developed as the final project of the Advanced Software Development course at Galway Technological Institute (GTI). It allows academic staff to manage and view information about students, teachers, groups, and modules.

The system is built as a multi-module Java application:
- The **desktop app** connects directly to a MySQL database and provides a graphical interface for staff to manage records
- The **backend** exposes a Spring Boot REST API, designed to allow any external client (mobile app, Angular PWA, etc.) to integrate with the system
- Shared **model** and **core** modules are reused across all components

---

## Project Structure

```
gti-record-rest/
├── model/          # Shared data model / domain classes
├── core/           # Shared business logic and utilities
├── backend/        # Spring Boot REST API server
├── desktop-app/    # Desktop client application (connects directly to MySQL)
├── android-app/    # Android client (currently excluded from build)
├── db/             # Database scripts / migrations
├── Dockerfile      # Docker image definition for the backend
├── Procfile        # Process definition (e.g. for cloud deployment)
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
| Containerization | Docker (OpenJDK 21 slim base image) |
| Database | MySQL |

---

## Prerequisites

- Java 21+
- Maven 3.9.4+
- MySQL database
- Docker (optional, for containerised deployment)

---

## Modules

### `model`
Contains shared domain/entity classes (Student, Teacher, Group, Module, User) used across the backend and desktop application.

### `core`
Contains shared business logic, service interfaces, and utility classes.

### `backend`
Spring Boot application exposing the REST API. Entry point: `ie.gti.asdl.rey.gtirecord.backend.GtiRecordRestServer`.

### `desktop-app`
Desktop client application with a graphical UI for managing student records. Connects directly to the MySQL database.

### `android-app`
Android client (currently excluded from the Maven build — work in progress).

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

## Running the Backend

After building, run the backend JAR directly:

```bash
java -jar backend/target/gti-record-backend-1.0.jar --spring.profiles.active=web
```

The server will start on **port 8080** by default.

---

## Docker *(in development)*

> ⚠️ **Note:** Docker support is an optional, experimental feature and has not been fully tested. The `Dockerfile` is provided as a starting point for containerised deployment but is not considered production-ready at this stage. Full instructions will be added once this functionality is stabilised.

---

## Usage

**Desktop application:**
1. Ensure MySQL is running and the database is set up (see `db/` folder for scripts)
2. Configure the connection in `application.properties`
3. Launch the desktop application — it connects directly to MySQL
4. Use the UI to browse and manage students, teachers, groups, and modules

To populate the database with test data, run the SQL scripts located in `db/sql_insert_scripts/` against your MySQL instance.

> Screenshots of the desktop application will be added here.

**REST API:**  
Start the backend server and send requests to `http://localhost:8080` from any HTTP client (browser, Postman, mobile app, Angular PWA, etc.).

To start the REST API server, run the following command from the project root after building:

```bash
java -jar backend/target/gti-record-backend-1.0.jar --spring.profiles.active=web
```

The API will be available at `http://localhost:8080`. See the [REST API](#rest-api) section for available endpoints.

---

## REST API

The backend exposes a RESTful API on `http://localhost:8080`.

> **Note:** The REST API is only active when the `web` Spring profile is enabled.  
> Start the server with: `java -jar gti-record-backend-1.0.jar --spring.profiles.active=web`

### Users

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `GET` | `/users/id/{id}` | Get user by numeric ID | `User` object or `404` |
| `GET` | `/users/name/{username}` | Get user by username | `User` object or `404` |

```
GET http://localhost:8080/users/id/1
GET http://localhost:8080/users/name/john
```

### Students

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `GET` | `/students` | Get all students | `List<Student>` |

### Teachers

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `GET` | `/teachers` | Get all teachers | `List<Teacher>` |

### Groups

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `GET` | `/groups` | Get all groups | `List<Group>` |

### Modules

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `GET` | `/modules` | Get all modules | `List<Module>` |
| `GET` | `/modules/teacher/{teacherPersonId}` | Get modules assigned to a specific teacher | `List<Module>` |

```
GET http://localhost:8080/modules/teacher/5
```

---

## AI Acknowledgment

AI tools were used during the development of this project:

- **Claude (Anthropic)**: Used to generate and refine this README documentation, including project structure analysis, API endpoint documentation, and formatting
- **ChatGPT (OpenAI)**: Assisted with implementing visual table styling in the desktop application (UI/UX code for table rendering)
- **JetBrains AI Assistant** (IntelliJ IDEA): Used for in-editor code suggestions and completions during development

All AI-generated suggestions were reviewed and tested before being included in the project.

---


## License

This project was developed as the final project of the Advanced Software Development course at Galway Technological Institute (GTI).
