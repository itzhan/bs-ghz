@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM 校园餐饮管理系统停止脚本 (Windows)

REM 项目配置
set "PROJECT_ROOT=%~dp0"
set "PIDS_DIR=%PROJECT_ROOT%.pids"

REM 端口配置
set "BACKEND_PORT=8085"
set "ADMIN_PORT=8848"
set "FRONTEND_PORT=5173"

REM 打印横幅
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                                                            ║
echo ║            校园餐饮管理系统                                  ║
echo ║           停止服务                                          ║
echo ║                                                            ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

echo [停止] 正在停止所有服务...
echo.

REM 按端口停止进程
echo [停止] 检查并清理端口占用...

for %%p in (%BACKEND_PORT% %ADMIN_PORT% %FRONTEND_PORT% 8849) do (
    echo [检查] 端口 %%p
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%%p " ^| findstr "LISTENING"') do (
        echo [停止] 停止进程 PID: %%a (端口: %%p)
        taskkill /F /PID %%a >nul 2>&1
        if errorlevel 1 (
            echo [警告] 无法停止进程 %%a
        ) else (
            echo [✓] 进程 %%a 已停止
        )
    )
)

REM 清理可能残留的 Java 进程（Spring Boot）
echo.
echo [清理] 检查 Spring Boot 进程...
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO LIST ^| findstr "PID:"') do (
    set "JAVA_PID=%%a"
    set "JAVA_PID=!JAVA_PID: =!"
    REM 检查是否是我们的 Spring Boot 进程（通过命令行参数判断）
    wmic process where "ProcessId=!JAVA_PID!" get CommandLine 2>nul | findstr /i "spring-boot" >nul 2>&1
    if not errorlevel 1 (
        echo [停止] 停止 Spring Boot 进程 (PID: !JAVA_PID!)
        taskkill /F /PID !JAVA_PID! >nul 2>&1
    )
)

REM 清理可能残留的 Node 进程（Vite）
echo.
echo [清理] 检查 Vite 进程...
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq node.exe" /FO LIST ^| findstr "PID:"') do (
    set "NODE_PID=%%a"
    set "NODE_PID=!NODE_PID: =!"
    REM 检查是否是我们的 Vite 进程
    wmic process where "ProcessId=!NODE_PID!" get CommandLine 2>nul | findstr /i "vite" >nul 2>&1
    if not errorlevel 1 (
        echo [停止] 停止 Vite 进程 (PID: !NODE_PID!)
        taskkill /F /PID !NODE_PID! >nul 2>&1
    )
)

REM 清理 PID 文件
if exist "%PIDS_DIR%" (
    echo.
    echo [清理] 清理 PID 文件...
    del /Q "%PIDS_DIR%\*.pid" >nul 2>&1
    echo [✓] PID 文件已清理
)

echo.
echo ═══════════════════════════════════════════════════════════
echo [✓] 所有服务已停止
echo ═══════════════════════════════════════════════════════════
echo.

timeout /t 2 >nul
exit /b 0
