#!/usr/bin/env pwsh
# 개발 환경 완전 시작 스크립트

#Requires -Version 7.0

$ErrorActionPreference = "Stop"

Write-Host "🚀 개발 환경 완전 시작..." -ForegroundColor Green

# 1. Docker 시작
Write-Host "🐳 Docker 환경 시작 중..." -ForegroundColor Blue
.\scripts\dev-run.ps1

# 2. 빌드 캐시 정리 (마이그레이션 파일 변경 감지를 위해)
Write-Host "🧹 빌드 캐시 정리 중..." -ForegroundColor Blue
.\gradlew clean

# 3. DB 완전 초기화 및 Flyway 마이그레이션 실행
Write-Host "🔄 DB 완전 초기화 중 (스키마 + Flyway 히스토리 삭제)..." -ForegroundColor Blue
docker exec web-demo-postgres-dev psql -U devuser -d devdb -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;" 2> $null

Write-Host "🔄 Flyway 마이그레이션 실행 중..." -ForegroundColor Blue
.\gradlew flywayMigrate

Write-Host ""
Write-Host "✅ 개발 환경 준비 완료!" -ForegroundColor Green
Write-Host "💡 이제 IDE에서 Spring Boot를 실행하세요!" -ForegroundColor Cyan
Write-Host "🌐 http://localhost:17070" -ForegroundColor Blue