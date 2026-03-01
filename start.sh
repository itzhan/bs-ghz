#!/bin/bash

# 校园餐饮管理系统启动脚本
# 支持 macOS 和 Linux

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 项目配置
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$PROJECT_ROOT/backend"
ADMIN_DIR="$PROJECT_ROOT/admin"
FRONTEND_DIR="$PROJECT_ROOT/frontend"
SQL_DIR="$PROJECT_ROOT/sql"
PIDS_DIR="$PROJECT_ROOT/.pids"
LOGS_DIR="$PROJECT_ROOT/.logs"

# 端口配置
BACKEND_PORT=8085
ADMIN_PORT=8848
FRONTEND_PORT=5173

# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=campus_dining
DB_USER=root
DB_PASS=ab123168

# 创建必要的目录
mkdir -p "$PIDS_DIR" "$LOGS_DIR"

# 打印横幅
print_banner() {
    echo ""
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                                                            ║${NC}"
    echo -e "${GREEN}║            ${YELLOW}校园餐饮管理系统${GREEN}                          ║${NC}"
    echo -e "${GREEN}║            ${YELLOW}Campus Dining Management System${GREEN}            ║${NC}"
    echo -e "${GREEN}║                                                            ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

# 检查命令是否存在
check_command() {
    if ! command -v "$1" &> /dev/null; then
        return 1
    fi
    return 0
}

# 检查环境
check_environment() {
    echo -e "${YELLOW}[检查] 检查运行环境...${NC}"
    
    # 检查 Java
    if ! check_command java; then
        echo -e "${RED}[错误] Java 未安装${NC}"
        echo "  请安装 Java 17 或更高版本:"
        echo "  macOS: brew install openjdk@17"
        echo "  Linux: sudo apt-get install openjdk-17-jdk"
        exit 1
    fi
    echo -e "${GREEN}[✓] Java: $(java -version 2>&1 | head -n 1)${NC}"
    
    # 检查 Maven
    if ! check_command mvn; then
        echo -e "${RED}[错误] Maven 未安装${NC}"
        echo "  请安装 Maven:"
        echo "  macOS: brew install maven"
        echo "  Linux: sudo apt-get install maven"
        exit 1
    fi
    echo -e "${GREEN}[✓] Maven: $(mvn -version | head -n 1)${NC}"
    
    # 检查 Node.js
    if ! check_command node; then
        echo -e "${RED}[错误] Node.js 未安装${NC}"
        echo "  请安装 Node.js:"
        echo "  macOS: brew install node"
        echo "  Linux: sudo apt-get install nodejs npm"
        exit 1
    fi
    echo -e "${GREEN}[✓] Node.js: $(node -v)${NC}"
    
    # 检查 pnpm
    if ! check_command pnpm; then
        echo -e "${YELLOW}[警告] pnpm 未安装，正在自动安装...${NC}"
        npm install -g pnpm
        if ! check_command pnpm; then
            echo -e "${RED}[错误] pnpm 安装失败${NC}"
            exit 1
        fi
    fi
    echo -e "${GREEN}[✓] pnpm: $(pnpm -v)${NC}"
    
    # 检查 MySQL 客户端
    if ! check_command mysql; then
        echo -e "${RED}[错误] MySQL 客户端未安装${NC}"
        echo "  请安装 MySQL 客户端:"
        echo "  macOS: brew install mysql-client"
        echo "  Linux: sudo apt-get install mysql-client"
        exit 1
    fi
    echo -e "${GREEN}[✓] MySQL 客户端已安装${NC}"
}

# 检查 MySQL 服务
check_mysql_service() {
    echo -e "${YELLOW}[检查] 检查 MySQL 服务...${NC}"
    
    # 尝试连接 MySQL
    if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "SELECT 1" &> /dev/null; then
        echo -e "${GREEN}[✓] MySQL 服务运行正常${NC}"
        return 0
    fi
    
    echo -e "${YELLOW}[警告] MySQL 服务未运行，尝试启动...${NC}"
    
    # 检测操作系统
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        if brew services start mysql 2>/dev/null; then
            echo -e "${GREEN}[✓] MySQL 服务已启动${NC}"
            sleep 2
        else
            echo -e "${RED}[错误] 无法启动 MySQL 服务${NC}"
            echo "  请手动启动: brew services start mysql"
            exit 1
        fi
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        if sudo systemctl start mysql 2>/dev/null || sudo service mysql start 2>/dev/null; then
            echo -e "${GREEN}[✓] MySQL 服务已启动${NC}"
            sleep 2
        else
            echo -e "${RED}[错误] 无法启动 MySQL 服务${NC}"
            echo "  请手动启动: sudo systemctl start mysql"
            exit 1
        fi
    else
        echo -e "${RED}[错误] 不支持的操作系统${NC}"
        exit 1
    fi
}

# 检查并初始化数据库
check_database() {
    echo -e "${YELLOW}[检查] 检查数据库...${NC}"
    
    # 检查数据库是否存在
    if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME" &> /dev/null; then
        # 检查表是否存在
        TABLE_COUNT=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "SHOW TABLES" 2>/dev/null | wc -l)
        if [ "$TABLE_COUNT" -gt 1 ]; then
            echo -e "${GREEN}[✓] 数据库 $DB_NAME 已存在且包含表${NC}"
            return 0
        fi
    fi
    
    echo -e "${YELLOW}[初始化] 数据库不存在或为空，正在初始化...${NC}"
    
    # 创建数据库
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci" 2>/dev/null
    
    # 导入初始化脚本
    if [ -f "$SQL_DIR/init.sql" ]; then
        echo -e "${YELLOW}[导入] 正在导入 init.sql...${NC}"
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" --default-character-set=utf8mb4 "$DB_NAME" < "$SQL_DIR/init.sql"
        echo -e "${GREEN}[✓] init.sql 导入完成${NC}"
    fi
    
    # 导入数据脚本
    if [ -f "$SQL_DIR/data.sql" ]; then
        echo -e "${YELLOW}[导入] 正在导入 data.sql...${NC}"
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" --default-character-set=utf8mb4 "$DB_NAME" < "$SQL_DIR/data.sql"
        echo -e "${GREEN}[✓] data.sql 导入完成${NC}"
    fi
    
    echo -e "${GREEN}[✓] 数据库初始化完成${NC}"
}

# 检查依赖
check_dependencies() {
    echo -e "${YELLOW}[检查] 检查项目依赖...${NC}"
    
    # 检查后端依赖
    if [ ! -d "$BACKEND_DIR/target/classes" ]; then
        echo -e "${YELLOW}[编译] 后端未编译，正在编译...${NC}"
        cd "$BACKEND_DIR"
        mvn compile -q
        cd "$PROJECT_ROOT"
        echo -e "${GREEN}[✓] 后端编译完成${NC}"
    else
        echo -e "${GREEN}[✓] 后端已编译${NC}"
    fi
    
    # 检查管理端依赖
    if [ ! -d "$ADMIN_DIR/node_modules" ]; then
        echo -e "${YELLOW}[安装] 管理端依赖未安装，正在安装...${NC}"
        cd "$ADMIN_DIR"
        pnpm install --silent
        cd "$PROJECT_ROOT"
        echo -e "${GREEN}[✓] 管理端依赖安装完成${NC}"
    else
        echo -e "${GREEN}[✓] 管理端依赖已安装${NC}"
    fi
    
    # 检查用户端依赖
    if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
        echo -e "${YELLOW}[安装] 用户端依赖未安装，正在安装...${NC}"
        cd "$FRONTEND_DIR"
        npm install --silent
        cd "$PROJECT_ROOT"
        echo -e "${GREEN}[✓] 用户端依赖安装完成${NC}"
    else
        echo -e "${GREEN}[✓] 用户端依赖已安装${NC}"
    fi
}

# 检查端口占用
check_ports() {
    echo -e "${YELLOW}[检查] 检查端口占用...${NC}"
    
    for port in $BACKEND_PORT $ADMIN_PORT $FRONTEND_PORT; do
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            echo -e "${RED}[错误] 端口 $port 已被占用${NC}"
            echo "  请停止占用该端口的进程或修改配置"
            exit 1
        fi
    done
    
    echo -e "${GREEN}[✓] 所有端口可用${NC}"
}

# 等待服务就绪
wait_for_service() {
    local port=$1
    local name=$2
    local max_attempts=30
    local attempt=0
    
    echo -e "${YELLOW}[等待] 等待 $name 启动...${NC}"
    
    while [ $attempt -lt $max_attempts ]; do
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            echo -e "${GREEN}[✓] $name 已就绪${NC}"
            return 0
        fi
        sleep 1
        attempt=$((attempt + 1))
    done
    
    echo -e "${RED}[错误] $name 启动超时${NC}"
    return 1
}

# 启动后端
start_backend() {
    echo -e "${YELLOW}[启动] 正在启动后端服务...${NC}"
    cd "$BACKEND_DIR"
    
    nohup mvn spring-boot:run > "$LOGS_DIR/backend.log" 2>&1 &
    BACKEND_PID=$!
    echo $BACKEND_PID > "$PIDS_DIR/backend.pid"
    
    cd "$PROJECT_ROOT"
    
    # 实时输出日志（带颜色）
    tail -f "$LOGS_DIR/backend.log" | sed "s/^/${RED}[后端]${NC} /" &
    echo $! > "$PIDS_DIR/backend_tail.pid"
    
    wait_for_service $BACKEND_PORT "后端服务"
}

# 启动管理端
start_admin() {
    echo -e "${YELLOW}[启动] 正在启动管理端...${NC}"
    cd "$ADMIN_DIR"
    
    nohup pnpm dev > "$LOGS_DIR/admin.log" 2>&1 &
    ADMIN_PID=$!
    echo $ADMIN_PID > "$PIDS_DIR/admin.pid"
    
    cd "$PROJECT_ROOT"
    
    # 实时输出日志（带颜色）
    tail -f "$LOGS_DIR/admin.log" | sed "s/^/${BLUE}[管理端]${NC} /" &
    echo $! > "$PIDS_DIR/admin_tail.pid"
    
    # 检查端口 8848，如果被占用则尝试 8849
    sleep 3
    if ! lsof -Pi :$ADMIN_PORT -sTCP:LISTEN -t >/dev/null 2>&1; then
        if lsof -Pi :8849 -sTCP:LISTEN -t >/dev/null 2>&1; then
            ADMIN_PORT=8849
            echo -e "${YELLOW}[提示] 端口 8848 被占用，使用端口 8849${NC}"
        fi
    fi
    
    wait_for_service $ADMIN_PORT "管理端"
}

# 启动用户端
start_frontend() {
    echo -e "${YELLOW}[启动] 正在启动用户端...${NC}"
    cd "$FRONTEND_DIR"
    
    nohup npm run dev > "$LOGS_DIR/frontend.log" 2>&1 &
    FRONTEND_PID=$!
    echo $FRONTEND_PID > "$PIDS_DIR/frontend.pid"
    
    cd "$PROJECT_ROOT"
    
    # 实时输出日志（带颜色）
    tail -f "$LOGS_DIR/frontend.log" | sed "s/^/${GREEN}[用户端]${NC} /" &
    echo $! > "$PIDS_DIR/frontend_tail.pid"
    
    wait_for_service $FRONTEND_PORT "用户端"
}

# 清理函数
cleanup() {
    echo ""
    echo -e "${YELLOW}[停止] 正在停止所有服务...${NC}"
    
    # 停止 tail 进程
    for pid_file in "$PIDS_DIR"/*_tail.pid; do
        if [ -f "$pid_file" ]; then
            kill $(cat "$pid_file") 2>/dev/null || true
        fi
    done
    
    # 停止主进程
    for pid_file in "$PIDS_DIR"/*.pid; do
        if [ -f "$pid_file" ] && [[ "$pid_file" != *_tail.pid ]]; then
            PID=$(cat "$pid_file")
            if ps -p $PID > /dev/null 2>&1; then
                kill $PID 2>/dev/null || true
            fi
        fi
    done
    
    # 按端口清理
    for port in $BACKEND_PORT $ADMIN_PORT $FRONTEND_PORT 8849; do
        lsof -ti :$port | xargs kill -9 2>/dev/null || true
    done
    
    echo -e "${GREEN}[✓] 所有服务已停止${NC}"
    exit 0
}

# 注册清理函数
trap cleanup INT TERM

# 主函数
main() {
    print_banner
    
    check_environment
    check_mysql_service
    check_database
    check_dependencies
    check_ports
    
    echo ""
    echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}开始启动服务...${NC}"
    echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
    echo ""
    
    start_backend
    start_admin
    start_frontend
    
    echo ""
    echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}所有服务启动成功！${NC}"
    echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
    echo ""
    echo -e "${YELLOW}访问地址：${NC}"
    echo -e "  ${RED}后端 API:${NC}    http://localhost:$BACKEND_PORT"
    echo -e "  ${BLUE}管理端:${NC}      http://localhost:$ADMIN_PORT"
    echo -e "  ${GREEN}用户端:${NC}      http://localhost:$FRONTEND_PORT"
    echo ""
    echo -e "${YELLOW}测试账号：${NC}"
    echo -e "  管理员: admin / 123456"
    echo -e "  商户: shanghu1~shanghu5 / 123456"
    echo -e "  普通用户: user1~user25 / 123456"
    echo ""
    echo -e "${YELLOW}日志文件：${NC}"
    echo -e "  后端: $LOGS_DIR/backend.log"
    echo -e "  管理端: $LOGS_DIR/admin.log"
    echo -e "  用户端: $LOGS_DIR/frontend.log"
    echo ""
    echo -e "${YELLOW}按 Ctrl+C 停止所有服务${NC}"
    echo ""
    
    # 保持脚本运行
    wait
}

# 运行主函数
main
