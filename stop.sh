#!/bin/bash
echo ""
echo "[停止] 正在停止所有服务..."
docker compose down
echo "[✓] 所有服务已停止"
