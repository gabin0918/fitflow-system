# FitFlow System

System zarządzania siłownią.

## Struktura

```
backend/
├── auth-service/        # Port 8081 - Użytkownicy i autoryzacja
├── gym-operations/      # Port 8082 - Zajęcia i rezerwacje
└── api-gateway/         # Port 8080 - API Gateway

frontend/                # Port 3000 - React

infrastructure/          # Docker Compose - bazy danych
```

### 1. Uruchomienie bazy danych
```bash
cd infrastructure
docker-compose up -d
```

### 2. Uruchomienie backend (osobne terminale)
```bash
# Auth Service
cd backend/auth-service/auth-service
mvn spring-boot:run

# Gym Operations
cd backend/gym-operations/gym-operations
mvn spring-boot:run
```

### 3. Uruchomienie frontend
```bash
cd frontend
npm install
npm start
```

## Bazy danych

| Baza | Port | Serwis |
|------|------|--------|
| auth_db | 5431 | Auth Service |
| operations_db | 5433 | Gym Operations |
| pgAdmin | 5050 | Web UI (admin@fitflow.com / admin) |

## API Documentation

- Auth Service: http://localhost:8081/swagger-ui.html
- Gym Operations: http://localhost:8082/swagger-ui.html

## Technologie

- Backend: Spring Boot 3.2.2, Java 17, PostgreSQL
- Frontend: React, CSS
- Infrastructure: Docker Compose
