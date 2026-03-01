@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM 校园餐饮管理系统启动脚本 (Windows)

REM 颜色定义（通过 cmd 窗口颜色）
REM 后端: 红色 (4F), 管理端: 蓝色 (1F), 用户端: 绿色 (2F)

REM 项目配置
set "PROJECT_ROOT=%~dp0"
set "BACKEND_DIR=%PROJECT_ROOT%backend"
set "ADMIN_DIR=%PROJECT_ROOT%admin"
set "FRONTEND_DIR=%PROJECT_ROOT%frontend"
set "SQL_DIR=%PROJECT_ROOT%sql"
set "PIDS_DIR=%PROJECT_ROOT%.pids"
set "LOGS_DIR=%PROJECT_ROOT%.logs"

REM 端口配置
set "BACKEND_PORT=8085"
set "ADMIN_PORT=8848"
set "FRONTEND_PORT=5173"

REM 数据库配置
set "DB_HOST=localhost"
set "DB_PORT=3306"
set "DB_NAME=campus_dining"
set "DB_USER=root"
set "DB_PASS=ab123168"

REM 创建必要的目录
if not exist "%PIDS_DIR%" mkdir "%PIDS_DIR%"
if not exist "%LOGS_DIR%" mkdir "%LOGS_DIR%"

REM 打印横幅
call :print_banner

REM 检查环境
call :check_environment
if errorlevel 1 exit /b 1

REM 检查 MySQL 服务
call :check_mysql_service
if errorlevel 1 exit /b 1

REM 检查并初始化数据库
call :check_database
if errorlevel 1 exit /b 1

REM 检查依赖
call :check_dependencies
if errorlevel 1 exit /b 1

REM 检查端口占用
call :check_ports
if errorlevel 1 exit /b 1

echo.
echo ═══════════════════════════════════════════════════════════
echo 开始启动服务...
echo ═══════════════════════════════════════════════════════════
echo.

REM 启动后端
call :start_backend

REM 启动管理端
call :start_admin

REM 启动用户端
call :start_frontend

echo.
echo ═══════════════════════════════════════════════════════════
echo 所有服务启动成功！
echo ═══════════════════════════════════════════════════════════
echo.
echo 访问地址：
echo   后端 API:    http://localhost:%BACKEND_PORT%
echo   管理端:      http://localhost:%ADMIN_PORT%
echo   用户端:      http://localhost:%FRONTEND_PORT%
echo.
echo 测试账号：
echo   管理员: admin / 123456
echo   商户: shanghu1~shanghu5 / 123456
echo   普通用户: user1~user25 / 123456
echo.
echo 日志文件：
echo   后端: %LOGS_DIR%\backend.log
echo   管理端: %LOGS_DIR%\admin.log
echo   用户端: %LOGS_DIR%\frontend.log
echo.
echo 按任意键关闭此窗口（将停止所有服务）...
pause >nul

REM 清理
call :cleanup
exit /b 0

:print_banner
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                                                            ║
echo ║            校园餐饮管理系统                                  ║
echo ║            Campus Dining Management System                 ║
echo ║                                                            ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
goto :eof

:check_environment
echo [检查] 检查运行环境...

REM 检查 Java
where java >nul 2>&1
if errorlevel 1 (
    echo [错误] Java 未安装
    echo   请安装 Java 17 或更高版本: https://adoptium.net/
    exit /b 1
)
for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VERSION=%%i
echo [✓] Java: %JAVA_VERSION%

REM 检查 Maven
where mvn >nul 2>&1
if errorlevel 1 (
    echo [错误] Maven 未安装
    echo   请安装 Maven: https://maven.apache.org/download.cgi
    exit /b 1
)
for /f "tokens=*" %%i in ('mvn -version 2^>^&1 ^| findstr /i "Apache Maven"') do set MAVEN_VERSION=%%i
echo [✓] Maven: %MAVEN_VERSION%

REM 检查 Node.js
where node >nul 2>&1
if errorlevel 1 (
    echo [错误] Node.js 未安装
    echo   请安装 Node.js: https://nodejs.org/
    exit /b 1
)
for /f "tokens=*" %%i in ('node -v') do set NODE_VERSION=%%i
echo [✓] Node.js: %NODE_VERSION%

REM 检查 pnpm
where pnpm >nul 2>&1
if errorlevel 1 (
    echo [警告] pnpm 未安装，正在自动安装...
    call npm install -g pnpm
    if errorlevel 1 (
        echo [错误] pnpm 安装失败
        exit /b 1
    )
)
for /f "tokens=*" %%i in ('pnpm -v') do set PNPM_VERSION=%%i
echo [✓] pnpm: %PNPM_VERSION%

REM 检查 MySQL 客户端
where mysql >nul 2>&1
if errorlevel 1 (
    echo [错误] MySQL 客户端未安装
    echo   请安装 MySQL: https://dev.mysql.com/downloads/mysql/
    exit /b 1
)
echo [✓] MySQL 客户端已安装
goto :eof

:check_mysql_service
echo [检查] 检查 MySQL 服务...

REM 尝试连接 MySQL
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% -e "SELECT 1" >nul 2>&1
if not errorlevel 1 (
    echo [✓] MySQL 服务运行正常
    goto :eof
)

echo [警告] MySQL 服务未运行，尝试启动...
net start MySQL >nul 2>&1
if errorlevel 1 (
    echo [错误] 无法启动 MySQL 服务
    echo   请手动启动: net start MySQL
    exit /b 1
)

timeout /t 2 >nul
echo [✓] MySQL 服务已启动
goto :eof

:check_database
echo [检查] 检查数据库...

REM 检查数据库是否存在
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% -e "USE %DB_NAME%" >nul 2>&1
if not errorlevel 1 (
    REM 检查表是否存在
    mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% -e "SHOW TABLES" >nul 2>&1
    if not errorlevel 1 (
        echo [✓] 数据库 %DB_NAME% 已存在且包含表
        goto :eof
    )
)

echo [初始化] 数据库不存在或为空，正在初始化...

REM 创建数据库
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% -e "CREATE DATABASE IF NOT EXISTS %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci" >nul 2>&1

REM 导入初始化脚本
if exist "%SQL_DIR%\init.sql" (
    echo [导入] 正在导入 init.sql...
    mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 %DB_NAME% < "%SQL_DIR%\init.sql"
    echo [✓] init.sql 导入完成
)

REM 导入数据脚本
if exist "%SQL_DIR%\data.sql" (
    echo [导入] 正在导入 data.sql...
    mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 %DB_NAME% < "%SQL_DIR%\data.sql"
    echo [✓] data.sql 导入完成
)

echo [✓] 数据库初始化完成
goto :eof

:check_dependencies
echo [检查] 检查项目依赖...

REM 检查后端依赖
if not exist "%BACKEND_DIR%\target\classes" (
    echo [编译] 后端未编译，正在编译...
    cd /d "%BACKEND_DIR%"
    call mvn compile -q
    cd /d "%PROJECT_ROOT%"
    echo [✓] 后端编译完成
) else (
    echo [✓] 后端已编译
)

REM 检查管理端依赖
if not exist "%ADMIN_DIR%\node_modules" (
    echo [安装] 管理端依赖未安装，正在安装...
    cd /d "%ADMIN_DIR%"
    call pnpm install --silent
    cd /d "%PROJECT_ROOT%"
    echo [✓] 管理端依赖安装完成
) else (
    echo [✓] 管理端依赖已安装
)

REM 检查用户端依赖
if not exist "%FRONTEND_DIR%\node_modules" (
    echo [安装] 用户端依赖未安装，正在安装...
    cd /d "%FRONTEND_DIR%"
    call npm install --silent
    cd /d "%PROJECT_ROOT%"
    echo [✓] 用户端依赖安装完成
) else (
    echo [✓] 用户端依赖已安装
)
goto :eof

:check_ports
echo [检查] 检查端口占用...

netstat -ano | findstr ":%BACKEND_PORT% " | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo [错误] 端口 %BACKEND_PORT% 已被占用
    exit /b 1
)

netstat -ano | findstr ":%ADMIN_PORT% " | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo [错误] 端口 %ADMIN_PORT% 已被占用
    exit /b 1
)

netstat -ano | findstr ":%FRONTEND_PORT% " | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo [错误] 端口 %FRONTEND_PORT% 已被占用
    exit /b 1
)

echo [✓] 所有端口可用
goto :eof

:start_backend
echo [启动] 正在启动后端服务...
cd /d "%BACKEND_DIR%"
start "后端服务 - 校园餐饮管理系统" cmd /k "color 4F && echo [后端服务] && mvn spring-boot:run"
cd /d "%PROJECT_ROOT%"
timeout /t 5 >nul
goto :eof

:start_admin
echo [启动] 正在启动管理端...
cd /d "%ADMIN_DIR%"
start "管理端 - 校园餐饮管理系统" cmd /k "color 1F && echo [管理端] && pnpm dev"
cd /d "%PROJECT_ROOT%"
timeout /t 3 >nul
goto :eof

:start_frontend
echo [启动] 正在启动用户端...
cd /d "%FRONTEND_DIR%"
start "用户端 - 校园餐饮管理系统" cmd /k "color 2F && echo [用户端] && npm run dev"
cd /d "%PROJECT_ROOT%"
timeout /t 3 >nul
goto :eof

:cleanup
echo.
echo [停止] 正在停止所有服务...

REM 按端口停止进程
for %%p in (%BACKEND_PORT% %ADMIN_PORT% %FRONTEND_PORT% 8849) do (
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%%p " ^| findstr "LISTENING"') do (
        taskkill /F /PID %%a >nul 2>&1
    )
)

echo [✓] 所有服务已停止
goto :eof
