#!/bin/bash

# 개발 환경 재시작 스크립트

set -e

echo "🔄 web-demo 개발 환경 재시작..."

# 완전 종료
echo "1️⃣ 기존 환경 종료 중..."
./scripts/stop-dev.sh

# 잠시 대기
echo "⏳ 잠시 대기 중..."
sleep 3

# 다시 시작
echo "2️⃣ 새 환경 시작 중..."
./scripts/run-dev.sh