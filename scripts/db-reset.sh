#!/bin/bash

# 데이터베이스 초기화 스크립트 (web-demo)

set -e

echo "🔄 web-demo 데이터베이스 초기화..."

# 프로젝트 루트 확인
if [ ! -f "build.gradle" ]; then
    echo "❌ 프로젝트 루트에서 실행해주세요."
    exit 1
fi

# 환경 선택
echo "초기화할 환경을 선택하세요:"
echo "1. 개발 환경 (dev)"
echo "2. 마스터 환경 (master) - ⚠️ 위험!"
read -p "선택 (1-2): " -n 1 -r
echo

case $REPLY in
    1)
        ENV="dev"
        COMPOSE_FILE="docker-compose.dev.yml"
        CONTAINER_NAME="web-demo-postgres-dev"
        echo "📝 개발 환경 초기화를 진행합니다."
        ;;
    2)
        ENV="master"
        COMPOSE_FILE="docker-compose.master.yml"
        CONTAINER_NAME="web-demo-postgres-master"
        echo "⚠️  마스터 환경 데이터를 초기화하려고 합니다."
        echo "   모든 데이터가 삭제됩니다!"
        read -p "정말로 계속하시겠습니까? (yes 입력): " confirm
        if [ "$confirm" != "yes" ]; then
            echo "❌ 취소되었습니다."
            exit 1
        fi
        ;;
    *)
        echo "❌ 잘못된 선택입니다."
        exit 1
        ;;
esac

# 환경 변수 로드
if [ -f .env ]; then
    export $(cat .env | grep -v '#' | grep -v '^$' | xargs)
fi

# 컨테이너 및 볼륨 삭제
echo "🧹 $ENV 환경 정리 중..."
cd docker
docker-compose -f $COMPOSE_FILE down -v --remove-orphans

# 마스터 환경인 경우 명명된 볼륨도 삭제
if [ "$ENV" = "master" ]; then
    echo "🗑️  영속 볼륨 삭제 중..."
    docker volume rm postgres_master_data 2>/dev/null || echo "볼륨이 존재하지 않습니다."
fi

cd ..

# Flyway 히스토리 초기화 옵션
read -p "Flyway 마이그레이션 히스토리도 초기화하시겠습니까? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🔄 Flyway 정리를 위해 환경을 임시 시작합니다..."

    # 환경 임시 시작
    cd docker
    docker-compose -f $COMPOSE_FILE up -d
    cd ..

    # DB 준비 대기
    sleep 15

    # Flyway 정리
    if [ "$ENV" = "dev" ]; then
        ./gradlew flywayClean \
            -Dflyway.url=jdbc:postgresql://localhost:5432/${DB_NAME:-web_demo_dev} \
            -Dflyway.user=${DB_USER:-devuser} \
            -Dflyway.password=${DB_PASSWORD:-devpass} || true
    else
        ./gradlew flywayClean \
            -Dflyway.url=jdbc:postgresql://localhost:5432/${MASTER_DB_NAME:-web_demo_master} \
            -Dflyway.user=${MASTER_DB_USER:-masteruser} \
            -Dflyway.password=${MASTER_DB_PASSWORD:-masterpass} || true
    fi

    # 환경 다시 정리
    cd docker
    docker-compose -f $COMPOSE_FILE down -v
    cd ..

    echo "✅ Flyway 히스토리 정리 완료"
fi

echo ""
echo "✅ $ENV 환경 초기화 완료!"
echo ""
echo "🚀 환경 재시작:"
echo "   개발 환경: ./scripts/dev-start.sh"
echo "   마스터 환경: ./scripts/master-start.sh"