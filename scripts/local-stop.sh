#!/bin/bash
# LOCAL 환경 Docker 완전 종료 스크립트

set -e  # 오류 발생 시 중단하지 않고 계속 정리

echo "LOCAL 환경 Docker 완전 종료 중..."

# 프로젝트 루트 확인
if [ ! -f "build.gradle" ]; then
    echo "X 프로젝트 루트에서 실행해주세요."
    exit 1
fi

# Spring Boot 종료 안내
echo "Spring Boot 종료 방법:"
echo "   - 실행 중인 터미널에서 Ctrl+C 를 눌러주세요"
echo "   - 또는 이 스크립트 실행 후 Ctrl+C 로 종료하세요"
echo ""

# Docker 환경 완전 정리
echo "Docker 환경 완전 정리 중..."

# Docker Compose로 중지 및 삭제
cd docker
echo "   Docker Compose로 서비스 중지 중..."
docker-compose -f docker-compose.local.yml down -v --remove-orphans

# 개별 컨테이너 강제 정리 (혹시 남아있을 경우)
cd ..
echo "   개별 컨테이너 정리 중..."
docker stop web-demo-postgres-local web-demo-adminer-local 2>/dev/null || true
docker rm web-demo-postgres-local web-demo-adminer-local 2>/dev/null || true

# 네트워크 정리
echo "   네트워크 정리 중..."
docker network rm web-demo-local-network 2>/dev/null || true

# 사용하지 않는 리소스 정리
echo "   사용하지 않는 Docker 리소스 정리 중..."
docker system prune -f --volumes

echo ""
echo "✓ LOCAL 환경 Docker 완전 정리 완료!"
echo ""
echo "정리된 항목:"
echo "   ✓ PostgreSQL 컨테이너 삭제"
echo "   ✓ Adminer 컨테이너 삭제"
echo "   ✓ 네트워크 정리"
echo "   ✓ 볼륨 정리"
echo "   ✓ 사용하지 않는 리소스 정리"
echo ""
echo "다음 실행: ./scripts/local-start.sh"