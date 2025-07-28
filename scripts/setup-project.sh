#!/bin/bash
# 개발 환경 완전 시작 스크립트 (Bash 버전)

set -e  # 오류 발생 시 중단

echo "🚀 개발 환경 완전 시작..."

# 프로젝트 루트 확인
if [[ ! -f "build.gradle" ]]; then
    echo "❌ 프로젝트 루트에서 실행해주세요."
    exit 1
fi

# M2 Pro 환경 체크
if [[ $(uname -m) == "arm64" ]]; then
    echo "✅ M2 Pro (arm64) 환경 확인됨"
else
    echo "⚠️  주의: M2 Pro 환경이 아닐 수 있습니다. 계속 진행합니다."
fi

# Docker 실행 체크
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker가 실행되지 않았습니다. Docker Desktop을 시작해주세요."
    exit 1
fi

# 필요한 디렉토리 생성
echo "📁 디렉토리 구조 생성 중..."
mkdir -p docker/init-scripts
mkdir -p docker/test-data
mkdir -p scripts
mkdir -p src/main/resources/db/migration

# 환경 파일 생성
if [ ! -f .env ]; then
    echo "📝 환경 파일 생성 중..."
    cat > .env << 'EOF'
# 개발 환경 설정
DB_NAME=web_demo_dev
DB_USER=devuser
DB_PASSWORD=devpass

# TODO: 운영 DB 연동 시 아래 마스터 환경 설정 주석 해제
# MASTER_DB_NAME=web_demo_master
# MASTER_DB_USER=masteruser
# MASTER_DB_PASSWORD=masterpass

# Spring 프로파일
SPRING_PROFILES_ACTIVE=dev

# Docker Compose 프로젝트명
COMPOSE_PROJECT_NAME=web-demo
EOF
    echo "✅ .env 파일이 생성되었습니다."
fi

# Gradle Wrapper 권한 설정
if [ -f "./gradlew" ]; then
    chmod +x ./gradlew
    echo "✅ Gradle Wrapper 권한 설정 완료"
fi

# 스크립트 권한 설정
chmod +x scripts/*.sh 2>/dev/null || true
echo "✅ 스크립트 실행 권한 설정 완료"

# build.gradle에 Flyway 설정 확인
if ! grep -q "flywaydb" build.gradle 2>/dev/null; then
    echo "⚠️  build.gradle에 Flyway 의존성을 추가해야 합니다."
    echo "   implementation 'org.flywaydb:flyway-core'"
    echo "   implementation 'org.flywaydb:flyway-database-postgresql'"
fi

echo "🎉 초기 설정이 완료되었습니다!"
echo ""
echo "📋 다음 단계:"
echo "1. build.gradle에 Flyway 의존성 확인"
echo "2. 개발 환경 시작: ./scripts/dev-start.sh"
echo "3. 마스터 환경 시작: ./scripts/master-start.sh"
echo "4. DB 초기화: ./scripts/db-reset.sh"
echo ""
echo "🔧 설정 파일 위치:"
echo "- 환경 변수: .env"
echo "- Docker 설정: docker/"
echo "- 마이그레이션: src/main/resources/db/migration/"