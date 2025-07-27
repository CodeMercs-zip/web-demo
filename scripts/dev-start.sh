#!/bin/bash
# 개발 환경 완전 시작 스크립트 (Bash 버전)

set -e  # 오류 발생 시 중단

echo "🚀 개발 환경 완전 시작..."

# 프로젝트 루트 확인
if [[ ! -f "build.gradle" ]]; then
    echo "❌ 프로젝트 루트에서 실행해주세요."
    exit 1
fi

# 1. Docker 시작
echo "🐳 Docker 환경 시작 중..."
./scripts/dev-run.sh

# 2. 빌드 캐시 정리 (마이그레이션 파일 변경 감지를 위해)
echo "🧹 빌드 캐시 정리 중..."
./gradlew clean

# 3. DB 완전 초기화 및 Flyway 마이그레이션 실행
echo "🔄 DB 완전 초기화 중 (스키마 + Flyway 히스토리 삭제)..."
docker exec web-demo-postgres-dev psql -U devuser -d devdb -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;" 2>/dev/null || true

echo "🔄 Flyway 마이그레이션 실행 중..."
./gradlew flywayMigrate

echo ""
echo "✅ 개발 환경 준비 완료!"
echo "💡 이제 IDE에서 Spring Boot를 실행하세요!"
echo "🌐 http://localhost:17070"