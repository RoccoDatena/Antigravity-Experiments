<div align="center">

# ⚡ Task Manager

### Full-Stack Task Management Application

[![Backend CI](https://github.com/RoccoDatena/Antigravity-Experiments/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/RoccoDatena/Antigravity-Experiments/actions/workflows/backend-ci.yml)
[![Frontend CI](https://github.com/RoccoDatena/Antigravity-Experiments/actions/workflows/frontend-ci.yml/badge.svg)](https://github.com/RoccoDatena/Antigravity-Experiments/actions/workflows/frontend-ci.yml)

[![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-red?style=flat-square&logo=angular)](https://angular.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

A modern, full-stack task management app with **JWT authentication**, **role-based access control**, **CI/CD pipelines**, and a **Glassmorphism** UI.

</div>

---

## ✨ Features

- 🔐 **JWT Authentication** — secure stateless login/signup
- 👥 **Role-Based Access** — Admin sees all tasks; Users see their own
- ✅ **Full CRUD** — create, edit, delete, quick-status tasks
- 🔍 **Live Filters** — filter by status, priority, and text search
- 📊 **Dashboard** — real-time stats with animated progress bars
- ⚠️ **Overdue Detection** — tasks past due date highlighted automatically
- 🛡️ **Admin Panel** — user management (Admin role only)
- 💎 **Glassmorphism UI** — dark gradient with frosted-glass cards

---

## 🏗️ Architecture

```
task-manager/            ← Angular 17 frontend (port 4200)
task-manager-backend/    ← Spring Boot 3 backend (port 8080)
```

```mermaid
graph LR
    A["Angular Frontend\n(port 4200)"] -- "HTTP + JWT Bearer" --> B["Spring Boot API\n(port 8080)"]
    B --> C[("H2 In-Memory DB")]
    B --> D["Spring Security\n(JWT Filter Chain)"]
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Angular 17, TypeScript, CSS (Glassmorphism) |
| Backend | Java 17+, Spring Boot 3.2, Spring Security |
| Database | H2 (in-memory, dev) |
| Auth | JWT (jjwt 0.11.5), BCrypt |
| Build | Maven (via wrapper), Angular CLI |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** — [Download Temurin](https://adoptium.net/temurin/releases/?version=17)
- **Node.js 18+** — [Download](https://nodejs.org/)

> **No global Maven installation required** — the project includes a Maven Wrapper (`mvnw.cmd`) that downloads Maven automatically on first run.

---

### 1. Clone the repository

```bash
git clone https://github.com/RoccoDatena/Antigravity-Experiments.git
cd task-manager
```

### 2. Start the Backend

```powershell
cd task-manager-backend
cmd /c mvnw.cmd spring-boot:run   # Windows
# OR
./mvnw spring-boot:run            # Linux / macOS
```

On first run, the wrapper downloads Maven 3.9.6 (~10 MB). Wait for:
```
Started TaskManagerApplication in X seconds
=== DataSeeder: 3 users and 6 tasks created ===
```

**Backend URL:** `http://localhost:8080`  
**H2 Console:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:taskmanagerdb`)

### 3. Start the Frontend

```bash
cd task-manager
npm install
npm start
```

**App URL:** `http://localhost:4200`

---

## 🔑 Demo Credentials

| Username | Password | Role |
|----------|----------|------|
| `admin` | `password` | Admin — full access, user management |
| `user1` | `password` | User — sees own tasks only |
| `user2` | `password` | User — sees own tasks only |

> ⚠️ Change credentials and the JWT secret before any production deployment.

---

## 📡 API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/auth/login` | Public | Login → returns JWT |
| `POST` | `/api/auth/signup` | Public | Register new user |
| `GET` | `/api/tasks` | Any | List tasks (filterable) |
| `POST` | `/api/tasks` | Any | Create task |
| `PUT` | `/api/tasks/{id}` | Owner / Admin | Update task |
| `DELETE` | `/api/tasks/{id}` | Owner / Admin | Delete task |
| `GET` | `/api/tasks/stats` | Any | Dashboard statistics |
| `GET` | `/api/users` | Admin only | List all users |

### Filter Query Params (`GET /api/tasks`)

| Param | Values |
|-------|--------|
| `status` | `TODO`, `IN_PROGRESS`, `DONE` |
| `priority` | `LOW`, `MEDIUM`, `HIGH` |
| `search` | Free text (matches title & description) |

---

## 📁 Project Structure

```
task-manager-backend/
├── src/main/java/com/.../
│   ├── controller/       # AuthController, TaskController, UserController
│   ├── service/          # TaskService, UserService (business logic)
│   ├── model/            # User, Task (JPA entities)
│   ├── repository/       # JPA Repositories with custom JPQL queries
│   ├── security/         # WebSecurityConfig, JwtUtils, JwtAuthFilter
│   ├── dto/              # Request/Response DTOs
│   └── seeder/           # DataSeeder (dev data)
└── src/main/resources/
    └── application.properties

task-manager/
└── src/app/
    ├── pages/            # login, register, dashboard, tasks, users
    ├── components/       # navbar, task-modal
    ├── services/         # AuthService, TaskService, UserService
    ├── guards/           # AuthGuard
    └── interceptors/     # JwtInterceptor
```

---

## ⚙️ Configuration

Edit `task-manager-backend/src/main/resources/application.properties`:

```properties
# Change before production!
app.jwt.secret=your-super-secret-key-min-32-chars
app.jwt.expiration=86400000   # 24 hours in ms

# Swap H2 for a real database (e.g. PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
```

---

## 🔄 DevOps & CI/CD

This project implements **Continuous Integration** using **GitHub Actions** with two independent pipelines that run automatically on every push and pull request.

### Pipeline Overview

```mermaid
graph LR
    A["git push / PR"] --> B{"GitHub Actions"}
    B --> C["Backend CI"]
    B --> D["Frontend CI"]
    C --> E["JDK 21 Setup"]
    E --> F["Maven Build & Test"]
    F --> G["Upload JAR Artifact"]
    D --> H["Node 20 Setup"]
    H --> I["npm ci"]
    I --> J["Production Build"]
    J --> K["Upload Dist Artifact"]
```

### Backend CI (`backend-ci.yml`)

| Step | Description |
|------|-------------|
| **Checkout** | Clones the repository |
| **JDK 21 (Temurin)** | Sets up Java with Maven dependency caching |
| **Build & Verify** | Runs `./mvnw clean verify` (compile + tests) |
| **Upload Artifact** | Stores the built `.jar` for 7 days |

### Frontend CI (`frontend-ci.yml`)

| Step | Description |
|------|-------------|
| **Checkout** | Clones the repository |
| **Node.js 20** | Sets up Node with npm dependency caching |
| **Install** | Clean install via `npm ci` |
| **Build** | Production build with ahead-of-time compilation |
| **Upload Artifact** | Stores the `dist/` bundle for 7 days |

> 💡 Both pipelines use **path filtering** — backend changes only trigger the backend pipeline, and vice versa.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "feat: add my feature"`
4. Push and open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
