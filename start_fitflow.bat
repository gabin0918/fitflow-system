@echo off
echo ====================================================
echo [0/5] CZYSZCZENIE PORTOW (Zamykanie starych sesji)
echo ====================================================
taskkill /f /im java.exe 2>nul
taskkill /f /im node.exe 2>nul

echo ====================================================
echo [1/5] START: Auth Service (8081)
echo ====================================================
cd backend\auth-service\auth-service
start "Auth-Service" cmd /c mvnw spring-boot:run
cd ..\..\..

echo Czekam 25 sekund na start Auth i SEED trenerow...
timeout /t 25

echo ====================================================
echo [2/5] START: Gym Operations (8082)
echo ====================================================
cd backend\gym-operations\gym-operations
start "Gym-Ops" cmd /c mvnw spring-boot:run
cd ..\..\..

echo ====================================================
echo [3/5] START: Membership Payment Service (8083)
echo ====================================================
cd backend\membership-payment-service\membership-payment-service
start "Payment-Service" cmd /c mvnw spring-boot:run
cd ..\..\..

echo ====================================================
echo [4/5] START: API Gateway (8080)
echo ====================================================
cd backend\api-gateway\api-gateway
start "API-Gateway" cmd /c mvnw spring-boot:run
cd ..\..\..

echo ====================================================
echo [5/5] START: Frontend (React)
echo ====================================================
cd frontend
start "React-Frontend" cmd /c npm start

echo.
echo WSZYSTKIE OKNA OTWARTE.
echo Jesli wciaz widzisz "Nieznany trener", odswiez strone za chwile.
pause