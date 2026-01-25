<<<<<<< Updated upstream
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
=======
# Infrastructure - FitFlow System

## Opis
Ten folder zawiera centralną konfigurację infrastruktury dla całego systemu FitFlow.

## Docker Compose

### Co zawiera `docker-compose.yml`
Plik konfiguracyjny uruchamiający wszystkie bazy danych dla mikroserwisów:

1. **auth-db** (Port: 5431)
   - Baza danych dla Auth Service
   - Database: `auth_db`

2. **gym-operations-db** (Port: 5432)
   - Baza danych dla Gym Operations Service
   - Database: `gym_operations_db`

3. **membership-payment-db** (Port: 5433)
   - Baza danych dla Membership & Payment Service
   - Database: `membership_payment_db`

4. **pgAdmin** (Port: 5050) - opcjonalne
   - Webowy interfejs do zarządzania bazami danych
   - Login: admin@fitflow.com
   - Hasło: admin

## Użycie

### Uruchomienie wszystkich baz danych
```bash
cd infrastructure
docker-compose up -d
```

### Zatrzymanie wszystkich baz danych
```bash
cd infrastructure
docker-compose down
```

### Sprawdzenie statusu
```bash
cd infrastructure
docker-compose ps
```

### Wyświetlenie logów
```bash
cd infrastructure
docker-compose logs -f
```

### Czyszczenie danych (usuwa wszystkie dane z baz!)
```bash
cd infrastructure
docker-compose down -v
```

## Porty

| Serwis | Port | Opis |
|--------|------|------|
| auth-db | 5431 | PostgreSQL - Auth Service |
| gym-operations-db | 5432 | PostgreSQL - Gym Operations |
| membership-payment-db | 5433 | PostgreSQL - Membership & Payment |
| pgAdmin | 5050 | Web UI dla PostgreSQL |

## Dostęp do baz danych

### Przez psql (CLI)
```bash
# Auth DB
docker exec -it auth-db psql -U admin -d auth_db

# Gym Operations DB
docker exec -it gym-operations-db psql -U admin -d gym_operations_db

# Membership Payment DB
docker exec -it membership-payment-db psql -U admin -d membership_payment_db
```

### Przez pgAdmin (Web UI)
1. Otwórz: http://localhost:5050
2. Login: admin@fitflow.com
3. Hasło: admin
4. Dodaj serwer:
   - Host: nazwa_kontenera (np. auth-db)
   - Port: 5432 (wewnętrzny port)
   - Username: admin
   - Password: admin_password

## Konfiguracja w serwisach

Każdy mikroserwis powinien używać odpowiedniego portu w `application.yaml`:

```yaml
# Auth Service
spring.datasource.url: jdbc:postgresql://localhost:5431/auth_db

# Gym Operations Service
spring.datasource.url: jdbc:postgresql://localhost:5432/gym_operations_db

# Membership & Payment Service
spring.datasource.url: jdbc:postgresql://localhost:5433/membership_payment_db
```

## Network

Wszystkie kontenery są podłączone do sieci `fitflow-network`, co umożliwia im komunikację między sobą.

## Health Checks

Każda baza danych ma skonfigurowany health check, który sprawdza czy jest gotowa do przyjmowania połączeń.

## Volumes

Dane są przechowywane w nazwanych volume'ach:
- `auth-data`
- `gym-operations-data`
- `membership-payment-data`

Dzięki temu dane nie są tracone po restarcie kontenerów.

## Troubleshooting

### Problem: Port już zajęty
```bash
# Sprawdź co używa portu
netstat -ano | findstr :5432

# Zmień port w docker-compose.yml
ports:
  - "5434:5432"  # zmiana z 5432 na 5434
```

### Problem: Kontener nie startuje
```bash
# Sprawdź logi
docker-compose logs <nazwa-serwisu>

# Przykład
docker-compose logs auth-db
```

### Problem: Brak połączenia z bazą
1. Sprawdź czy kontener działa: `docker-compose ps`
2. Sprawdź health check: `docker inspect <container-id>`
3. Sprawdź logi: `docker-compose logs <service-name>`
4. Sprawdź czy port jest otwarty: `telnet localhost 5431`

## Backup i Restore

### Backup
```bash
# Auth DB
docker exec auth-db pg_dump -U admin auth_db > backup_auth.sql

# Gym Operations DB
docker exec gym-operations-db pg_dump -U admin gym_operations_db > backup_gym.sql

# Membership Payment DB
docker exec membership-payment-db pg_dump -U admin membership_payment_db > backup_membership.sql
```

### Restore
```bash
# Auth DB
docker exec -i auth-db psql -U admin auth_db < backup_auth.sql

# Gym Operations DB
docker exec -i gym-operations-db psql -U admin gym_operations_db < backup_gym.sql

# Membership Payment DB
docker exec -i membership-payment-db psql -U admin membership_payment_db < backup_membership.sql
```

## Wymagania

- Docker Desktop 20.10+
- Docker Compose 2.0+
- Minimum 2GB RAM dla wszystkich kontenerów
- Porty 5431, 5432, 5433, 5050 muszą być wolne
>>>>>>> Stashed changes
