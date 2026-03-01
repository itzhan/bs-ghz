@echo off
chcp 65001 >nul
set "PROJECT_ROOT=%~dp0"

echo.
echo  Starting Campus Dining Management System...
echo.

:: Start Backend
echo [1/3] Starting backend service...
cd /d "%PROJECT_ROOT%backend"
start "Backend" cmd /k "mvn spring-boot:run"
cd /d "%PROJECT_ROOT%"
timeout /t 5 >nul

:: Start Admin
echo [2/3] Starting admin panel...
cd /d "%PROJECT_ROOT%admin"
start "Admin" cmd /k "pnpm dev"
cd /d "%PROJECT_ROOT%"
timeout /t 3 >nul

:: Start Frontend
echo [3/3] Starting frontend...
cd /d "%PROJECT_ROOT%frontend"
start "Frontend" cmd /k "npm run dev"
cd /d "%PROJECT_ROOT%"

echo.
echo  All services started!
echo  Backend:  http://localhost:8085
echo  Admin:    http://localhost:8848
echo  Frontend: http://localhost:5173
echo.
pause
