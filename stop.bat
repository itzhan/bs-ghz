@echo off
chcp 65001 >nul
cd /d "%~dp0"
docker compose down
echo.
echo  All services stopped.
echo.
pause
