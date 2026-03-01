#!/bin/bash

# 校园餐饮管理系统停止脚本
# 支持 macOS 和 Linux

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 项目配置
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PIDS_DIR="$PROJECT_ROOT/.pids"

# 端口配置
BACKEND_PORT=8085
ADMIN_PORT=8848
FRONTEND_PORT=5173

# 打印横幅
print_banner() {
    echo ""
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                                                            ║${NC}"
    echo -e "${GREEN}║            ${YELLOW}校园餐饮管理系统${GREEN}                          ║${NC}"
    echo -e "${GREEN}║            ${YELLOW}停止服务${GREEN}                                ║${NC}"
    echo -e "${GREEN}║                                                            ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

# 主函数
main() {
    print_banner
    
    echo -e "${YELLOW}[停止] 正在停止所有服务...${NC}"
    echo ""
    
    # 停止 tail 监控进程
    if [ -d "$PIDS_DIR" ]; then
        for pid_file in "$PIDS_DIR"/*_tail.pid; do
            if [ -f "$pid_file" ]; then
                PID=$(cat "$pid_file" 2>/dev/null || echo "")
                if [ -n "$PID" ] && ps -p $PID > /dev/null 2>&1; then
                    echo -e "${YELLOW}[停止] 停止日志监控进程 (PID: $PID)${NC}"
                    kill $PID 2>/dev/null || true
                fi
            fi
        done
    fi
    
    # 停止主进程（通过 PID 文件）
    if [ -d "$PIDS_DIR" ]; then
        for pid_file in "$PIDS_DIR"/*.pid; do
            if [ -f "$pid_file" ] && [[ "$pid_file" != *_tail.pid ]]; then
                PID=$(cat "$pid_file" 2>/dev/null || echo "")
                if [ -n "$PID" ] && ps -p $PID > /dev/null 2>&1; then
                    SERVICE_NAME=$(basename "$pid_file" .pid)
                    echo -e "${YELLOW}[停止] 停止 $SERVICE_NAME (PID: $PID)${NC}"
                    kill $PID 2>/dev/null || true
                    sleep 1
                    # 如果进程仍在运行，强制杀死
                    if ps -p $PID > /dev/null 2>&1; then
                        kill -9 $PID 2>/dev/null || true
                    fi
                fi
            fi
        done
    fi
    
    # 按端口停止进程（备用方案）
    echo ""
    echo -e "${YELLOW}[停止] 检查并清理端口占用...${NC}"
    
    for port in $BACKEND_PORT $ADMIN_PORT $FRONTEND_PORT 8849; do
        PIDS=$(lsof -ti :$port 2>/dev/null || echo "")
        if [ -n "$PIDS" ]; then
            for pid in $PIDS; do
                echo -e "${YELLOW}[停止] 停止占用端口 $port 的进程 (PID: $pid)${NC}"
                kill -9 $pid 2>/dev/null || true
            done
        fi
    done
    
    # 清理 PID 文件
    if [ -d "$PIDS_DIR" ]; then
        rm -f "$PIDS_DIR"/*.pid
        echo -e "${GREEN}[清理] PID 文件已清理${NC}"
    fi
    
    echo ""
    echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}[✓] 所有服务已停止${NC}"
    echo -e "${GREEN}═══════════════════════════════════════════════════════════${NC}"
    echo ""
}

# 运行主函数
main
