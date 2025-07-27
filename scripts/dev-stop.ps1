#!/usr/bin/env pwsh
# 개발 환경 완전 종료 스크립트 (PowerShell 7.5 버전)

#Requires -Version 7.0

param()

$ErrorActionPreference = "Continue"  # Stop에서 Continue로 변경 (정리 작업은 계속 진행)
$ProgressPreference = "SilentlyContinue"

Write-Host "🛑 web-demo 개발 환경 완전 종료 중..." -ForegroundColor Red

# 프로젝트 루트 확인
if (-not (Test-Path "build.gradle"))
{
    Write-Host "❌ 프로젝트 루트에서 실행해주세요." -ForegroundColor Red
    return
}

# 시작 메시지
Write-Host "🧹 Docker 환경만 정리합니다 (Spring Boot는 IDE에서 별도 종료)" -ForegroundColor Cyan

# Docker 환경 완전 정리
Write-Host "🐳 Docker 환경 완전 정리 중..." -ForegroundColor Blue

# Docker Compose로 중지 및 삭제 (볼륨 포함)
if (Test-Path "docker")
{
    Push-Location docker
    Write-Host "   🔄 Docker Compose로 서비스 중지 중 (볼륨 포함)..." -ForegroundColor Yellow
    try
    {
        if (Test-Path "docker-compose.dev.yml")
        {
            docker-compose -f docker-compose.dev.yml down -v --remove-orphans 2> $null
            Write-Host "   ✅ Docker Compose 서비스 중지 완료 (볼륨 삭제됨)" -ForegroundColor Green
        }
        else
        {
            Write-Host "   ⚠️ docker-compose.dev.yml 파일을 찾을 수 없습니다" -ForegroundColor Yellow
        }
    }
    catch
    {
        Write-Host "   ⚠️ Docker Compose 종료 중 오류 발생: $_" -ForegroundColor Yellow
    }
    Pop-Location
}
else
{
    Write-Host "   ⚠️ docker 폴더를 찾을 수 없습니다" -ForegroundColor Yellow
}

# 개별 컨테이너 강제 정리
Write-Host "   🧹 개별 컨테이너 정리 중..." -ForegroundColor Yellow
$containers = @("web-demo-postgres-dev", "web-demo-adminer-dev")

foreach ($container in $containers)
{
    try
    {
        $stopResult = docker stop $container 2> $null
        if ($LASTEXITCODE -eq 0)
        {
            Write-Host "     ✅ $container 중지됨" -ForegroundColor Green
        }

        $rmResult = docker rm $container 2> $null
        if ($LASTEXITCODE -eq 0)
        {
            Write-Host "     ✅ $container 삭제됨" -ForegroundColor Green
        }
    }
    catch
    {
        Write-Host "     ⚠️ $container 정리 중 오류: $_" -ForegroundColor Yellow
    }
}

# dev 환경에서는 볼륨도 완전 삭제 (완전한 초기화를 위해)
Write-Host "   📦 PostgreSQL 볼륨 삭제 중 (dev 환경 완전 초기화)..." -ForegroundColor Yellow
try
{
    docker volume rm web-demo_postgres-dev-data 2> $null
    if ($LASTEXITCODE -eq 0)
    {
        Write-Host "     ✅ PostgreSQL 볼륨 삭제됨" -ForegroundColor Green
    }
    else
    {
        Write-Host "     ⚠️ PostgreSQL 볼륨이 존재하지 않거나 이미 삭제됨" -ForegroundColor Yellow
    }
}
catch
{
    Write-Host "     ⚠️ 볼륨 삭제 중 오류: $_" -ForegroundColor Yellow
}


Write-Host ""
Write-Host "✅ Docker 환경 정리 완료!" -ForegroundColor Green
Write-Host "🚀 다음 실행: .\scripts\dev-start.ps1" -ForegroundColor Cyan