#!/bin/bash

# 마스터(운영) 환경 시작 스크립트 (web-demo)

set -e

echo "🏭 web-demo 마스터 환경 시작 중..."

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

# 마스터 환경 확인
echo "⚠️  마스터 환경을 시작합니다."
echo "   - 데이터가 영속화됩니다."
echo "   - 볼륨이 생성되어 데이터가 보존됩니다."
read -p "계속하시겠습니까? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ 취소되었습니다."
    exit 1
fi

# 마스터 환경 시작
echo "🚀 PostgreSQL 마스터 환경 시작 중..."
cd docker
docker-compose -f docker-compose.master.yml up -d
cd ..

# 헬스체크 대기
echo "⏳ PostgreSQL 헬스체크 대기 중..."
timeout=90
while [ $timeout -gt 0 ]; do
    if docker exec web-demo-postgres-master pg_isready -U ${MASTER_DB_USER:-masteruser} > /dev/null 2>&1; then
        echo "✅ PostgreSQL 연결 성공!"
        break
    fi
    printf "."
    sleep 3
    timeout=$((timeout - 3))
done
echo

if [ $timeout -le 0 ]; then
    echo "❌ PostgreSQL 연결 시간 초과"
    echo "로그 확인: docker logs web-demo-postgres-master"
    exit 1
fi

# Flyway 마이그레이션 실행
if [ -d "src/main/resources/db/migration" ] && [ "$(ls -A src/main/resources/db/migration)" ]; then
    echo "🔄 Flyway 마이그레이션 실행 중..."
    ./gradlew flywayMigrate \
        -Dflyway.url=jdbc:postgresql://localhost:5432/${MASTER_DB_NAME:-web_demo_master} \
        -Dflyway.user=${MASTER_DB_USER:-masteruser} \
        -Dflyway.password=${MASTER_DB_PASSWORD:-masterpass}
    echo "✅ 마이그레이션 완료!"
else
    echo "ℹ️  마이그레이션 파일이 없습니다."
fi

echo ""
echo "🎉 마스터 환경 준비 완료!"
echo ""
echo "📊 접속 정보:"
echo "   🗄️  PostgreSQL: localhost:5432"
echo "   📋 DB: ${MASTER_DB_NAME:-web_demo_master}"
echo "   👤 User: ${MASTER_DB_USER:-masteruser}"
echo ""
echo "🚀 Spring Boot 실행:"
echo "   ./gradlew bootRun --args='--spring.profiles.active=master'"
echo ""
echo "🔧 유용한 명령어:"
echo "   - 로그 확인: docker logs web-demo-postgres-master"
echo "   - 볼륨 확인: docker volume ls"
echo "   - 환경 중지: cd docker && docker-compose -f docker-compose.master.yml down"
echo ""
echo "⚠️  주의: 마스터 환경의 데이터는 영속화됩니다."