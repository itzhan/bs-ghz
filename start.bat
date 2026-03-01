@echo off
chcp 65001 >nul
cd /d "%~dp0"
docker compose up 
echo.
echo  Backend:  http://localhost:8085
echo  Admin:    http://localhost:8848
echo  Frontend: http://localhost:5173
echo.
pause
