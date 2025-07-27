#!/usr/bin/env pwsh
# 개발 환경 완전 종료 스크립트 (PowerShell 7.5 버전)

#Requires -Version 7.0

param()

$ErrorActionPreference = "Continue"  # Stop에서 Continue로 변경 (정리 작업은 계속 진행)
$ProgressPreference = "SilentlyContinue"

Write-Host "🛑 web-demo 개발 환경 완전 종료 중..." -ForegroundColor Red

# 프로젝트 루트 확인
if (-not (Test-Path "build.gradle")) {
    Write-Host "❌ 프로젝트 루트에서 실행해주세요." -ForegroundColor Red
    return
}

# Spring Boot 종료 안내
$shutdownInfo = @"
📋 Spring Boot 종료 방법:
   - 실행 중인 터미널에서 Ctrl+C 를 눌러주세요
   - 또는 이 스크립트 실행 후 Ctrl+C 로 종료하세요

"@

Write-Host $shutdownInfo -ForegroundColor Cyan

# Docker 환경 완전 정리
Write-Host "🐳 Docker 환경 완전 정리 중..." -ForegroundColor Blue

# Docker Compose로 중지 및 삭제
if (Test-Path "docker") {
    Push-Location docker
    Write-Host "   🔄 Docker Compose로 서비스 중지 중..." -ForegroundColor Yellow
    try {
        if (Test-Path "docker-compose.dev.yml") {
            docker-compose -f docker-compose.dev.yml down -v --remove-orphans 2>$null
            Write-Host "   ✅ Docker Compose 서비스 중지 완료" -ForegroundColor Green
        } else {
            Write-Host "   ⚠️ docker-compose.dev.yml 파일을 찾을 수 없습니다" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "   ⚠️ Docker Compose 종료 중 오류 발생: $_" -ForegroundColor Yellow
    }
    Pop-Location
} else {
    Write-Host "   ⚠️ docker 폴더를 찾을 수 없습니다" -ForegroundColor Yellow
}

# 개별 컨테이너 강제 정리
Write-Host "   🧹 개별 컨테이너 정리 중..." -ForegroundColor Yellow
$containers = @("web-demo-postgres-dev", "web-demo-adminer-dev")

foreach ($container in $containers) {
    try {
        $stopResult = docker stop $container 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "     ✅ $container 중지됨" -ForegroundColor Green
        }

        $rmResult = docker rm $container 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "     ✅ $container 삭제됨" -ForegroundColor Green
        }
    } catch {
        Write-Host "     ⚠️ $container 정리 중 오류: $_" -ForegroundColor Yellow
    }
}

# 볼륨 정리 (선택적)
Write-Host "   📦 관련 볼륨 정리 중..." -ForegroundColor Yellow
try {
    $volumes = docker volume ls -q | Where-Object { $_ -like "*web-demo*" }
    if ($volumes) {
        foreach ($volume in $volumes) {
            docker volume rm $volume 2>$null | Out-Null
            if ($LASTEXITCODE -eq 0) {
                Write-Host "     ✅ 볼륨 $volume 삭제됨" -ForegroundColor Green
            }
        }
    }
} catch {
    Write-Host "     ⚠️ 볼륨 정리 중 오류: $_" -ForegroundColor Yellow
}

# 네트워크 정리
Write-Host "   🌐 네트워크 정리 중..." -ForegroundColor Yellow
try {
    $networks = docker network ls -q --filter name=web-demo
    if ($networks) {
        foreach ($network in $networks) {
            docker network rm $network 2>$null | Out-Null
            if ($LASTEXITCODE -eq 0) {
                Write-Host "     ✅ 네트워크 정리됨" -ForegroundColor Green
            }
        }
    }
} catch {
    Write-Host "     ⚠️ 네트워크 정리 중 오류: $_" -ForegroundColor Yellow
}

# 사용하지 않는 리소스 정리 (선택적)
Write-Host "   🗑️  사용하지 않는 Docker 리소스 정리 중..." -ForegroundColor Yellow
try {
    $pruneResult = docker system prune -f --volumes 2>$null
    Write-Host "     ✅ 사용하지 않는 리소스 정리 완료" -ForegroundColor Green
} catch {
    Write-Host "     ⚠️ 리소스 정리 중 오류: $_" -ForegroundColor Yellow
}

# 최종 상태 확인
Write-Host "`n   🔍 Docker 컨테이너 상태 확인..." -ForegroundColor Yellow
$runningContainers = docker ps --filter name=web-demo --format "table {{.Names}}\t{{.Status}}"
if ($runningContainers -and $runningContainers.Count -gt 1) {
    Write-Host "     ⚠️ 아직 실행 중인 web-demo 컨테이너가 있습니다:" -ForegroundColor Yellow
    Write-Host $runningContainers
} else {
    Write-Host "     ✅ 모든 web-demo 컨테이너가 정리되었습니다" -ForegroundColor Green
}

# 완료 메시지
$completionInfo = @"

✅ Docker 환경 완전 정리 완료!

📊 정리된 항목:
   ✅ PostgreSQL 컨테이너 중지/삭제
   ✅ Adminer 컨테이너 중지/삭제
   ✅ 관련 볼륨 정리
   ✅ 네트워크 정리
   ✅ 사용하지 않는 리소스 정리

🚀 다음 실행: .\scripts\run-dev.ps1
💡 Spring Boot 애플리케이션이 실행 중이라면 Ctrl+C로 종료해주세요

"@

Write-Host $completionInfo -ForegroundColor Green

# 포트 사용 상태 확인 (추가 정보)
Write-Host "📊 포트 사용 상태:" -ForegroundColor Cyan
try {
    $ports = @(5432, 8080, 17070)
    foreach ($port in $ports) {
        $connection = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
        if ($connection) {
            $processName = (Get-Process -Id $connection.OwningProcess -ErrorAction SilentlyContinue).ProcessName
            Write-Host "   🔴 포트 $port 사용 중 (프로세스: $processName)" -ForegroundColor Red
        } else {
            Write-Host "   ✅ 포트 $port 사용 가능" -ForegroundColor Green
        }
    }
} catch {
    Write-Host "   ⚠️ 포트 상태 확인 중 오류" -ForegroundColor Yellow
}