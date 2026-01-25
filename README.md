# FitFlow System

System zarządzania siłownią - projekt studencki.

## Struktura projektu

```
fitflow-system/
├── backend/
│   ├── auth-service/                # Port 8081 - Użytkownicy i autoryzacja
│   ├── gym-operations/              # Port 8082 - Zajęcia i rezerwacje
│   ├── membership-payment-service/  # Port 8083 - Karnety i płatności
│   └── api-gateway/                 # Port 8080 - API Gateway
│
├── frontend/                        # Port 3000 - React
│
└── infrastructure/                  # Docker Compose - bazy danych PostgreSQL
```

## Jak uruchomić projekt

### 1. Uruchomienie bazy danych (Docker)

```bash
cd infrastructure
docker-compose up -d
```

To uruchomi:
- **auth_db** (Port 5431) - baza danych dla Auth Service
- **operations_db** (Port 5433) - baza danych dla Gym Operations
- **membership_payment_db** (Port 5434) - baza danych dla Membership & Payment
- **pgAdmin** (Port 5050) - interfejs webowy do zarządzania bazami

### 2. Uruchomienie backend (3 osobne terminale)

**Terminal 1 - Auth Service:**
```bash
cd backend/auth-service/auth-service
mvn spring-boot:run
```

**Terminal 2 - Gym Operations:**
```bash
cd backend/gym-operations/gym-operations
mvn spring-boot:run
```

**Terminal 3 - Membership & Payment:**
```bash
cd backend/membership-payment-service/membership-payment-service
mvn spring-boot:run
```

### 3. Uruchomienie frontend

**Terminal 4:**
```bash
cd frontend
npm install    # tylko przy pierwszym uruchomieniu
npm start
```

## Dostęp do aplikacji

| Komponent | URL |
|-----------|-----|
| Frontend | http://localhost:3000 |
| Auth Service (Swagger) | http://localhost:8081/swagger-ui.html |
| Gym Operations (Swagger) | http://localhost:8082/swagger-ui.html |
| Membership & Payment (Swagger) | http://localhost:8083/swagger-ui.html |
| pgAdmin | http://localhost:5050 (admin@fitflow.com / admin) |

## Zatrzymanie aplikacji

- **Backend/Frontend** - naciśnij `Ctrl+C` w każdym terminalu
- **Bazy danych:**
  ```bash
  cd infrastructure
  docker-compose down
  ```

## Technologie

**Backend:**
- Spring Boot 3.2.2
- Java 17
- Spring Data JPA
- PostgreSQL
- Lombok
- Swagger/OpenAPI

**Frontend:**
- React
- JavaScript
- CSS (bez frameworków CSS)

**Infrastructure:**
- Docker Compose
- PostgreSQL 15

## Bazy danych

Projekt używa trzech oddzielnych baz danych PostgreSQL, zarządzanych centralnie przez `infrastructure/docker-compose.yml`:

| Baza | Port | Użycie |
|------|------|--------|
| auth_db | 5431 | Użytkownicy, role, autoryzacja |
| operations_db | 5433 | Zajęcia, rezerwacje, lokalizacje, sprzęt |
| membership_payment_db | 5434 | Karnety, plany członkostwa, płatności |
