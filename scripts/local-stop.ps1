#!/usr/bin/env pwsh
# LOCAL 환경 Docker 완전 종료 스크립트

#Requires -Version 7.0

param()

$ErrorActionPreference = "Continue"  # 정리 작업은 계속 진행
$ProgressPreference = "SilentlyContinue"

Write-Host "LOCAL 환경 Docker 완전 종료 중..."

# 프로젝트 루트 확인
if (-not (Test-Path "build.gradle"))
{
    Write-Host "X 프로젝트 루트에서 실행해주세요."
    return
}

# 시작 메시지
Write-Host "Docker 환경만 정리합니다 (Spring Boot는 IDE에서 별도 종료)"

# Docker 환경 완전 정리
Write-Host "Docker 환경 완전 정리 중..."

# Docker Compose로 중지 및 삭제 (볼륨 포함)
if (Test-Path "docker")
{
    Push-Location docker
    Write-Host "   Docker Compose로 서비스 중지 중 (볼륨 포함)..."
    try
    {
        if (Test-Path "docker-compose.local.yml")
        {
            docker-compose -f docker-compose.local.yml down -v --remove-orphans 2> $null
            Write-Host "   ✓ Docker Compose 서비스 중지 완료 (볼륨 삭제됨)"
        }
        else
        {
            Write-Host "   docker-compose.local.yml 파일을 찾을 수 없습니다"
        }
    }
    catch
    {
        Write-Host "   Docker Compose 종료 중 오류 발생: $_"
    }
    Pop-Location
}
else
{
    Write-Host "   docker 폴더를 찾을 수 없습니다"
}

# 개별 컨테이너 강제 정리
Write-Host "   개별 컨테이너 정리 중..."
$containers = @("web-demo-postgres-local", "web-demo-adminer-local")

foreach ($container in $containers)
{
    try
    {
        $stopResult = docker stop $container 2> $null
        if ($LASTEXITCODE -eq 0)
        {
            Write-Host "     ✓ $container 중지됨"
        }

        $rmResult = docker rm $container 2> $null
        if ($LASTEXITCODE -eq 0)
        {
            Write-Host "     ✓ $container 삭제됨"
        }
    }
    catch
    {
        Write-Host "     $container 정리 중 오류: $_"
    }
}

# LOCAL 환경에서는 볼륨도 완전 삭제 (완전한 초기화를 위해)
Write-Host "   PostgreSQL 볼륨 삭제 중 (LOCAL 환경 완전 초기화)..."
try
{
    docker volume rm web-demo_postgres-local-data 2> $null
    if ($LASTEXITCODE -eq 0)
    {
        Write-Host "     ✓ PostgreSQL 볼륨 삭제됨"
    }
    else
    {
        Write-Host "     PostgreSQL 볼륨이 존재하지 않거나 이미 삭제됨"
    }
}
catch
{
    Write-Host "     볼륨 삭제 중 오류: $_"
}


Write-Host ""
Write-Host "✓ LOCAL 환경 Docker 정리 완료!"
Write-Host "다음 실행: .\scripts\local-start.ps1"