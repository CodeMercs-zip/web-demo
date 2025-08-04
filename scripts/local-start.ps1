#!/usr/bin/env pwsh
# LOCAL 환경 완전 초기화 스크립트 - Docker + DB 초기화 + Flyway 마이그레이션
# 로컬 개발용으로만 사용, DEV/PROD 환경에서는 사용 금지

#Requires -Version 7.0

$ErrorActionPreference = "Stop"

Write-Host "LOCAL 환경 완전 초기화 시작..."

# 1. Docker 환경 시작
Write-Host "Docker 환경 시작 중..."
.\scripts\local-run.ps1

# 2. 빌드 캐시 정리 (마이그레이션 파일 변경 감지를 위해)
Write-Host "빌드 캐시 정리 중..."
.\gradlew clean

# 3. DB 완전 초기화 (LOCAL 환경에서만 허용)
Write-Host "DB 완전 초기화 중 (스키마 + Flyway 히스토리 삭제)..."
docker exec web-demo-postgres-local psql -U localuser -d localdb -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;" 2> $null

# 4. Flyway 마이그레이션 실행
Write-Host "Flyway 마이그레이션 실행 중..."
.\gradlew flywayMigrate

Write-Host ""
Write-Host "✓ LOCAL 환경 완전 초기화 완료!"
Write-Host "IDE에서 Spring Boot를 실행하세요 (--spring.profiles.active=local)"
Write-Host "애플리케이션 URL: http://localhost:17070"
Write-Host ""
Write-Host "경고: 이 스크립트는 LOCAL 환경에서만 사용하세요!"
Write-Host "      DEV/PROD 환경에서는 데이터가 손실될 수 있습니다."