#!/bin/bash
# 개발 환경 통합 실행 스크립트 (Bash 버전)

set -e  # 오류 발생 시 중단

echo "🚀 web-demo 통합 개발 환경 시작..."

# 프로젝트 루트 확인
if [ ! -f "build.gradle" ]; then
    echo "❌ 프로젝트 루트에서 실행해주세요."
    exit 1
fi

# 환경 변수 로드
if [ -f .env ]; then
    export $(cat .env | grep -v '#' | grep -v '^$' | xargs)
    echo "✅ 환경 변수 로드됨"
fi

# 기존 컨테이너 강제 정리
echo "🧹 기존 컨테이너 정리 중..."
docker stop web-demo-postgres-dev web-demo-adminer-dev 2>/dev/null || true
docker rm web-demo-postgres-dev web-demo-adminer-dev 2>/dev/null || true

# Docker Compose로도 정리
cd docker
docker-compose -f docker-compose.dev.yml down -v --remove-orphans 2>/dev/null || true

# Docker 환경 시작
echo "🐳 PostgreSQL Docker 환경 시작 중..."
docker-compose -f docker-compose.dev.yml up -d
cd ..

# PostgreSQL 준비 대기
echo "⏳ PostgreSQL 연결 대기 중..."
timeout=60
while [ $timeout -gt 0 ]; do
    if docker exec web-demo-postgres-dev pg_isready -U ${DB_USER:-devuser} > /dev/null 2>&1; then
        echo "✅ PostgreSQL 연결 성공!"
        break
    fi
    printf "."
    sleep 2
    timeout=$((timeout - 2))
done

if [ $timeout -le 0 ]; then
    echo "❌ PostgreSQL 연결 시간 초과"
    exit 1
fi

echo ""
echo "✅ Docker 환경 준비 완료!"
echo "🌐 Adminer: http://localhost:8080/?pgsql=postgres-dev&username=devuser&password=devpass&db=devdb"
echo "💡 이제 IDE에서 Spring Boot 실행하세요!"