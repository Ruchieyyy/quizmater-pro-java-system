# QuizMaster Pro - Smart Quiz Management System

## Overview

QuizMaster Pro is a desktop-based quiz management platform developed using Java Swing and Object-Oriented Programming principles. The system provides secure role-based access, quiz management, leaderboard tracking, score analytics, and MySQL-backed data persistence through JDBC integration.

---

## Project Highlights

- Built a desktop-based quiz platform inspired by modern online assessment systems.
- Integrated MySQL database using JDBC for persistent data storage.
- Implemented CRUD operations for question management and user administration.
- Developed leaderboard tracking and quiz analytics functionality.
- Applied Object-Oriented Programming principles with modular architecture.
  
---

## Features

- Secure Login & Registration System
- Role-Based Admin and Student Access
- Question Management (Add, Edit, Delete)
- MySQL Database Integration
- JDBC-Based Data Access Layer
- Leaderboard & Ranking System
- Real-Time Quiz Scoring
- Timed Quiz Assessments
- Quiz Attempt History Tracking
- Dark-Themed Interactive UI 

---

## Tech Stack

- Java
- Java Swing
- JDBC
- MySQL
- SQL
- Object-Oriented Programming (OOP)
- Prepared Statements
  
---

## Project Structure

```text
quizmater-pro-java-system/
│
├── src/
│   ├── AdminPanel.java
│   ├── DatabaseManager.java
│   ├── LeaderboardPanel.java
│   ├── LoginPanel.java
│   ├── Main.java
│   └── other source files
│
├── screenshots/
│   ├── login-page.png
│   ├── admin-dashboard.png
│   ├── add-question.png
│   ├── leaderboard.png
│   └── quiz-result.png
│
├── README.md
└── .gitignore
```

---

## Database Setup

### Create Database

```sql
CREATE DATABASE quizmaster;
```

### Required Tables

- users
- questions
- quiz_attempts

Update database credentials inside:

```text
src/DatabaseManager.java
```

```java
private static final String URL = "jdbc:mysql://localhost:3306/quizmaster";
private static final String DB_USER = "root";
private static final String DB_PASS = "your_password";
```

---

## How to Run

1. Install MySQL Server and MySQL Workbench.
2. Create the `quizmaster` database.
3. Import the required tables.
4. Update database credentials in `DatabaseManager.java`.
5. Open the project in Eclipse or IntelliJ IDEA.
6. Run:

```text
src/Main.java
```

---

## Screenshots

### Login Page
![Login Page](QuizMaster/screenshots/login-page.png)

### Admin Dashboard
![Admin Dashboard](QuizMaster/screenshots/admin-dashboard.png)

### Add Question Panel
![Add Question](QuizMaster/screenshots/add-question.png)

### Leaderboard
![Leaderboard](QuizMaster/screenshots/leaderboard.png)

### Quiz Result Screen
![Quiz Results](QuizMaster/screenshots/quiz-result.png)

---

## System Modules

### Admin Features

- Manage quiz questions
- Monitor users
- View leaderboard analytics
- Track quiz attempts

### Student Features

- Attempt quizzes
- View rankings
- Analyze scores
- Track performance

---

## System Modules

### Admin Module

- Add Questions
- Edit Questions
- Delete Questions
- View Users
- Monitor Quiz Activity

### Student Module

- Register and Login
- Attempt Timed Quizzes
- View Scores
- Track Ranking

### Leaderboard Module

- Score Tracking
- User Ranking
- Attempt History

### Database Layer

- JDBC Connectivity
- MySQL Integration
- Prepared Statements
- CRUD Operations

## Author

### Ruchi Shukla

- GitHub: https://github.com/Ruchieyyy
- LinkedIn: https://www.linkedin.com/in/ruchi-shukla-731051309
