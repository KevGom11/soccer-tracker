# ⚽ Soccer Tracker

> A full-stack web application that lets users follow live and upcoming soccer matches across multiple leagues, subscribe to favorite teams, and receive match updates — built with **React + Vite** (frontend) and **Spring Boot** (backend).

---

## 📖 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
    - [Prerequisites](#prerequisites)
    - [Environment Variables](#environment-variables)
    - [Running the App](#running-the-app)
- [API Integration](#-api-integration)
- [Database Schema](#-database-schema)
- [Preview](#-preview)
- [Contributing](#-contributing)
- [Future Improvements](#-future-improvements)
- [Author](#-author)

---

## ✨ Features
- View **live and upcoming matches** from 12+ major soccer leagues
- Filter matches by **league** (Premier League, La Liga, Serie A, etc.)
- **User authentication** with email verification
- **Subscriptions** — follow your favorite teams and receive updates
- Modern, responsive **UI built with Tailwind CSS**
- Backend powered by **Spring Boot** and **PostgreSQL**
- Clean architecture with **DTOs**, **services**, and **repositories**

---

## 🧩 Tech Stack

**Frontend**
- React + Vite + TypeScript
- Tailwind CSS
- Axios
- React Router v6

**Backend**
- Spring Boot 3.x
- Java 21
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway (DB Migrations)
- Lombok

**Other**
- Football-Data API (Free Tier)
- Docker (optional setup)
- IntelliJ IDEA / VS Code

---

## 📁 Project Structure

soccer-tracker/
├── backend/
│ ├── src/
│ │ └── main/
│ │ ├── java/com/kevin/soccertracker/
│ │ │ ├── controller/
│ │ │ ├── domain/
│ │ │ ├── dto/
│ │ │ ├── repo/
│ │ │ └── service/
│ │ └── resources/
│ │ ├── application.yml
│ │ └── db/migration/
│ ├── build.gradle
│ └── settings.gradle
│
├── frontend/
│ ├── src/
│ │ ├── api/
│ │ ├── components/
│ │ ├── pages/
│ │ ├── App.tsx
│ │ └── main.tsx
│ ├── package.json
│ ├── tailwind.config.js
│ ├── vite.config.ts
│ └── tsconfig.json
│
├── docker-compose.yml (optional)
└── README.md


---

## 🚀 Getting Started

### Prerequisites
Make sure you have the following installed:
- **Node.js** ≥ 18
- **npm** or **yarn**
- **Java 21**
- **Gradle** or use the included Gradle wrapper
- **PostgreSQL**

---

### Environment Variables
Create the following files based on the examples provided:

**Frontend – `.env`**
VITE_API_BASE_URL=http://localhost:8080
VITE_FOOTBALL_API_KEY=your-football-data-api-key



**Backend – `application.yml`**
```
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/soccertracker
    username: postgres
    password: yourpassword
  jpa:
    hibernate:
      ddl-auto: update
  flyway:
    enabled: true
football:
  api:
    key: your-football-data-api-key
🏃 Running the App
Backend
bash
cd backend
./gradlew bootRun
Backend runs on:
➡️ http://localhost:8080

Frontend
bash
cd frontend
npm install
npm run dev
Frontend runs on:
➡️ http://localhost:5173

⚡ API Integration
This app uses the Football-Data.org API to fetch:

Match schedules

Team details

Competition standings

⚠️ The free tier may not include all endpoints such as previous games or live scores.

🗄️ Database Schema (Simplified)
Table	Description
users	Stores user information and authentication codes
subscriptions	Maps users to teams they follow
teams	Stores league/team details
matches	Caches match data from the Football API

🖼️ Preview
(Add screenshots inside the /assets folder and reference them here.)



🧠 Future Improvements
Add real-time match updates using WebSockets

Implement email notifications for subscribed teams

Add admin dashboard for match management

Improve UI animations and transitions

Support mobile app version (React Native)

🤝 Contributing
Fork the repository

Create your feature branch (git checkout -b feature/new-feature)

Commit your changes (git commit -m 'Add new feature')

Push to your branch (git push origin feature/new-feature)

Open a Pull Request

👤 Author
Kevin Gomes

💻 Computer Science @ Kennesaw State University

🌐 https://www.linkedin.com/in/kevin-gomes-intern2026/

📫 Contact: kevgom11@gmail.com