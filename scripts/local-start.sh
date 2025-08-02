#!/bin/bash
# LOCAL 환경 완전 초기화 스크립트 - Docker + DB 초기화 + Flyway 마이그레이션
# 로컬 개발용으로만 사용, DEV/PROD 환경에서는 사용 금지

set -e  # 오류 발생 시 중단

echo "LOCAL 환경 완전 초기화 시작..."

# 프로젝트 루트 확인
if [[ ! -f "build.gradle" ]]; then
    echo "X 프로젝트 루트에서 실행해주세요."
    exit 1
fi

# 1. Docker 환경 시작
echo "Docker 환경 시작 중..."
./scripts/local-run.sh

# 2. 빌드 캐시 정리 (마이그레이션 파일 변경 감지를 위해)
echo "빌드 캐시 정리 중..."
./gradlew clean

# 3. DB 완전 초기화 (LOCAL 환경에서만 허용)
echo "DB 완전 초기화 중 (스키마 + Flyway 히스토리 삭제)..."
docker exec web-demo-postgres-local psql -U localuser -d localdb -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;" 2>/dev/null || true

# 4. Flyway 마이그레이션 실행
echo "Flyway 마이그레이션 실행 중..."
./gradlew flywayMigrate

echo ""
echo "✓ LOCAL 환경 완전 초기화 완료!"
echo "IDE에서 Spring Boot를 실행하세요 (--spring.profiles.active=local)"
echo "애플리케이션 URL: http://localhost:17070"
echo ""
echo "경고: 이 스크립트는 LOCAL 환경에서만 사용하세요!"
echo "      DEV/PROD 환경에서는 데이터가 손실될 수 있습니다."