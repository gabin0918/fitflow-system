# Infrastructure

Bazy danych dla systemu FitFlow.

## Uruchomienie

```bash
docker-compose up -d
```

## Co zawiera

- **auth-db** (Port 5431) - baza dla Auth Service
- **operations-db** (Port 5433) - baza dla Gym Operations
- **pgAdmin** (Port 5050) - Web UI dla baz danych

## pgAdmin

URL: http://localhost:5050  
Login: admin@fitflow.com  
Hasło: admin

## Zatrzymanie

```bash
docker-compose down
```

## Dostęp przez CLI

```bash
# Auth DB
docker exec -it fitflow-auth-db psql -U admin -d auth_db

# Operations DB
docker exec -it fitflow-ops-db psql -U admin -d operations_db
```
