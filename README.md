# ⚡ FocusFlow

A full-stack Pomodoro productivity app that helps you focus, track tasks, and visualize your weekly progress — built with vanilla JavaScript, Spring Boot, and MySQL.

🌐 **Live App:** https://prutha-13.github.io/focusflow/  
🔌 **API:** https://focusflow-production-22b8.up.railway.app/api/v1/tasks
![FocusFlow](screenshot.png)

---

## ✨ Features

- **Pomodoro Timer** — 25/5/15 minute work and break cycles with visual ring progress
- **Task Queue** — Add, complete, and delete tasks; tasks persist in MySQL across sessions
- **Pomodoro Tracking** — Each completed session is credited to the selected task
- **Weekly Productivity Chart** — Bar chart showing tasks completed and pomodoros logged per day
- **Session History** — Visual dots tracking today's completed focus sessions
- **Fully Deployed** — Frontend on GitHub Pages, backend on Railway with MySQL

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Frontend | HTML, CSS, Vanilla JavaScript, Chart.js |
| Backend | Java 17, Spring Boot 3.2, Maven |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation |
| Frontend Host | GitHub Pages |
| Backend Host | Railway |

---

## 📁 Project Structure

```
focusflow/
├── index.html                  # Frontend — Pomodoro timer, task queue, weekly chart
└── backend/
    ├── pom.xml
    └── src/main/java/com/focusflow/
        ├── entity/
        │   └── Task.java                    # JPA entity mapped to MySQL tasks table
        ├── repository/
        │   └── TaskRepository.java          # JPA queries — filter, search, weekly stats
        ├── service/
        │   └── TaskService.java             # Business logic — CRUD, pomodoro, archive
        ├── controller/
        │   └── TaskController.java          # REST endpoints at /api/v1/tasks
        ├── dto/
        │   ├── TaskRequestDTO.java
        │   ├── TaskResponseDTO.java
        │   └── WeeklyStatsDTO.java
        ├── exception/
        │   ├── TaskNotFoundException.java
        │   └── GlobalExceptionHandler.java
        └── config/
            └── CorsConfig.java              # Global CORS for GitHub Pages
```

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/tasks` | Get all tasks (supports `?status=`, `?search=`, `?activeOnly=true`) |
| GET | `/api/v1/tasks/{id}` | Get single task |
| POST | `/api/v1/tasks` | Create a task |
| PUT | `/api/v1/tasks/{id}` | Update a task |
| PATCH | `/api/v1/tasks/{id}/complete` | Mark task as completed |
| PATCH | `/api/v1/tasks/{id}/pomodoro` | Log a Pomodoro session to a task |
| PATCH | `/api/v1/tasks/{id}/archive` | Archive a task |
| DELETE | `/api/v1/tasks/{id}` | Delete a task |
| GET | `/api/v1/tasks/stats/weekly` | Get weekly productivity stats for chart |

---

## 🚀 Running Locally

### Prerequisites
- Java 17+
- Maven
- MySQL 8

### 1. Set up the database

```bash
mysql -u root -p < backend/schema.sql
```

This creates the `focusflow_db` database, user, and `tasks` table.

### 2. Configure the backend

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/focusflow_db
spring.datasource.username=focusflow_user
spring.datasource.password=your_password_here
```

### 3. Run the Spring Boot server

```bash
cd backend
mvn spring-boot:run
```

Server starts at `http://localhost:8080`.

### 4. Open the frontend

Open `index.html` with Live Server in VS Code (or any local server at port 5500).  
Make sure `API_BASE` in `index.html` points to `http://localhost:8080/api/v1`.

---

## 🧪 Running Tests

```bash
cd backend
mvn test
```

Tests cover: task creation, default priority, completing a task, pomodoro increment, delete, active task filtering, and not-found exceptions.

---

## 🌍 Deployment

| Service | Platform | URL |
|---|---|---|
| Frontend | GitHub Pages | https://prutha-13.github.io/focusflow/ |
| Backend | Railway | https://focusflow-production-22b8.up.railway.app |
| Database | Railway MySQL | Managed via Railway environment variables |

---

## 📌 Task Model

```json
{
  "id": 1,
  "title": "Build Spring Boot API",
  "description": null,
  "status": "COMPLETED",
  "priority": "HIGH",
  "pomodoroCount": 3,
  "estimatedPomodoros": 4,
  "createdAt": "2026-03-10T09:12:10",
  "updatedAt": "2026-03-10T10:42:39",
  "completedAt": "2026-03-10T10:42:39"
}
```

**Status values:** `PENDING` · `IN_PROGRESS` · `COMPLETED` · `ARCHIVED`  
**Priority values:** `LOW` · `MEDIUM` · `HIGH` · `URGENT`

---

## 👩‍💻 Author

**Prutha** — [github.com/Prutha-13](https://github.com/Prutha-13)
