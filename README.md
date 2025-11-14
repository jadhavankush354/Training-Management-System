<<<<<<< HEAD
# 🎓 Training Management System

A full-stack web application for managing training programs, trainers, trainees, batches, courses, and attendance tracking with analytics dashboard.

---

## 🚀 Features

### Core Modules

- **Trainer Management** - Add, edit, delete trainer availability with location and time slots
- **Trainee Management** - Enroll, edit, delete trainees and assign them to batches
- **Batch Management** - Create, edit, delete training batches with trainer assignment
- **Course Management** - Add, edit, delete courses with categories and duration
- **Attendance Tracking** - Mark, edit, delete attendance with batch filtering
- **Analytics Dashboard** - View trainer occupancy, batch enrollments, and attendance trends with charts

---

## 🛠️ Tech Stack

### Frontend
- Vue.js 3
- Vue Router 4
- Axios
- Chart.js
- Nginx

### Backend
- Java 17
- Spring Boot 3.5.7
- Spring Data JPA
- Maven 3.9.6

### Database
- MariaDB 10.11

### DevOps
- Docker
- Docker Compose

---

## 📦 Prerequisites

- Docker Desktop (v24.0 or higher)
- Docker Compose (v3.8 or higher)
- Git

---

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/jadhavankush354/Training-Management-System.git
cd Training-Management-System
```

### 2. Start the Application
```bash
docker-compose up --build
```

Wait for all containers to start (takes about 1-2 minutes on first run).

### 3. Access the Application

- **Frontend:** http://localhost
- **Backend API:** http://localhost:8081/api
- **Database:** localhost:3307

### 4. Stop the Application
```bash
docker-compose down
```

### 5. Clean Up (Remove all data)
```bash
docker-compose down -v
```

---

## 📁 Project Structure
```
Training Management System/
├── management/                    # Spring Boot Backend
│   ├── src/main/java/
│   │   └── com/training/management/
│   │       ├── controller/        # REST APIs
│   │       ├── service/           # Business Logic
│   │       ├── repository/        # Database Access
│   │       └── model/             # Entity Models
│   ├── pom.xml
│   └── Dockerfile
│
├── training_management_frontend/  # Vue.js Frontend
│   ├── src/
│   │   ├── components/            # Vue Components
│   │   ├── router/                # Routes
│   │   └── services/              # API Services
│   ├── package.json
│   ├── nginx.conf
│   └── Dockerfile
│
├── database/
│   └── init.sql                   # Database Schema & Sample Data
│
├── docker-compose.yml
└── README.md
```

---

## 🔌 API Endpoints

### Trainers
- `GET /api/trainers` - Get all trainers
- `POST /api/trainers/availability` - Add trainer
- `PUT /api/trainers/{id}` - Update trainer
- `DELETE /api/trainers/{id}` - Delete trainer

### Trainees
- `GET /api/trainees` - Get all trainees
- `POST /api/trainees/enroll` - Enroll trainee
- `PUT /api/trainees/{id}` - Update trainee
- `DELETE /api/trainees/{id}` - Delete trainee
- `GET /api/trainees/batch/{batchId}` - Get trainees by batch

### Batches
- `GET /api/batches` - Get all batches
- `POST /api/batches` - Create batch
- `PUT /api/batches/{id}` - Update batch
- `DELETE /api/batches/{id}` - Delete batch

### Courses
- `GET /api/courses` - Get all courses
- `POST /api/courses` - Create course
- `PUT /api/courses/{id}` - Update course
- `DELETE /api/courses/{id}` - Delete course

### Attendance
- `GET /api/attendance` - Get all attendance
- `POST /api/attendance/mark` - Mark attendance
- `PUT /api/attendance/{id}` - Update attendance
- `DELETE /api/attendance/{id}` - Delete attendance
- `GET /api/attendance/batch/{batchId}` - Get by batch

### Reports
- `GET /api/reports/trainer-occupancy` - Trainer occupancy stats
- `GET /api/reports/batch-enrollments` - Batch enrollment distribution
- `GET /api/reports/attendance-trends` - Attendance trends over time

---

## 🗄️ Database

The database is automatically initialized with sample data:
- 7 Trainers
- 11 Trainees
- 5 Batches
- 10 Courses
- Sample attendance records

---

## 💻 Local Development (Without Docker)

### Backend
```bash
cd management
mvn clean install
mvn spring-boot:run
```

Backend runs on: http://localhost:8081

### Frontend
```bash
cd training_management_frontend
npm install
npm run serve
```

Frontend runs on: http://localhost:8080

### Database

Install MariaDB and run:
```bash
mysql -u root -p < database/init.sql
```

---

## 🐛 Troubleshooting

### Port Already in Use
If ports 80, 8081, or 3307 are in use:
```bash
docker-compose down
# Stop other services using these ports
docker-compose up
```

### Docker Not Running
Start Docker Desktop and wait for it to be ready, then run:
```bash
docker-compose up --build
```

### Database Connection Issues
```bash
docker-compose logs db
```

---

## 📧 Contact

**Ankush Jadhav**
- Email: jadhavankush354@gmail.com
- GitHub: [@jadhavankush354](https://github.com/jadhavankush354)
- LinkedIn: [Ankush Jadhav](https://www.linkedin.com/in/ankush-jadhav-397258261)

---

## 📝 Project Info

**Purpose:** Interview Project 2025  
**Status:** Complete & Production Ready

---

<p align="center">Built with Spring Boot, Vue.js, and Docker</p>
=======
# Training-Management-System
>>>>>>> 23af08205e16124ad40f47439d13c2a24aba67b6
