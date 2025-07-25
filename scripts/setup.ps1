# 프로젝트 Docker 환경 초기 설정 스크립트
# web-demo 프로젝트용 (PowerShell - Windows/Mac/Linux)

param(
    [switch]$Force
)

Write-Host "🚀 web-demo PostgreSQL Docker 환경 설정 시작..." -ForegroundColor Green

# 운영체제 및 아키텍처 확인
$OS = $PSVersionTable.OS
$Architecture = $env:PROCESSOR_ARCHITECTURE
if ([string]::IsNullOrEmpty($Architecture)) {
    $Architecture = [System.Runtime.InteropServices.RuntimeInformation]::ProcessArchitecture
}

Write-Host "🖥️  운영체제: $OS" -ForegroundColor Cyan
Write-Host "🔧 아키텍처: $Architecture" -ForegroundColor Cyan

# 현재 디렉토리가 프로젝트 루트인지 확인
if (-not (Test-Path "build.gradle")) {
    Write-Host "❌ 프로젝트 루트 디렉토리에서 실행해주세요." -ForegroundColor Red
    Read-Host "Enter 키를 눌러 종료하세요"
    exit 1
}

# 아키텍처 체크
switch ($Architecture) {
    "ARM64" {
        Write-Host "✅ ARM64 환경 확인됨 (M1/M2/M3 Mac 또는 ARM Windows)" -ForegroundColor Green
    }
    "AMD64" {
        Write-Host "✅ x86_64 환경 확인됨 (Intel 기반 시스템)" -ForegroundColor Green
    }
    default {
        Write-Host "⚠️  알 수 없는 아키텍처: $Architecture. 계속 진행합니다." -ForegroundColor Yellow
    }
}

# Docker 실행 체크
try {
    $null = docker info 2>$null
    Write-Host "✅ Docker 실행 확인됨" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker가 실행되지 않았습니다." -ForegroundColor Red
    if ($IsWindows -or $env:OS -eq "Windows_NT") {
        Write-Host "   Docker Desktop for Windows를 시작해주세요." -ForegroundColor Yellow
    } elseif ($IsMacOS) {
        Write-Host "   Docker Desktop for Mac을 시작해주세요." -ForegroundColor Yellow
    } else {
        Write-Host "   Docker 서비스를 시작해주세요." -ForegroundColor Yellow
    }
    Read-Host "Enter 키를 눌러 종료하세요"
    exit 1
}

# 필요한 디렉토리 생성
Write-Host "📁 디렉토리 구조 생성 중..." -ForegroundColor Blue

$directories = @(
    "docker/init-scripts",
    "docker/test-data",
    "scripts",
    "src/main/resources/db/migration"
)

foreach ($dir in $directories) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        Write-Host "   ✅ 생성됨: $dir" -ForegroundColor Green
    }
}

# 환경 파일 생성
if (-not (Test-Path ".env") -or $Force) {
    Write-Host "📝 환경 파일 생성 중..." -ForegroundColor Blue

    $envContent = @"
# 개발 환경 설정
DB_NAME=web_demo_dev
DB_USER=devuser
DB_PASSWORD=devpass

# 마스터 환경 설정
MASTER_DB_NAME=web_demo_master
MASTER_DB_USER=masteruser
MASTER_DB_PASSWORD=masterpass

# Spring 프로파일
SPRING_PROFILES_ACTIVE=dev

# Docker Compose 프로젝트명
COMPOSE_PROJECT_NAME=web-demo
"@

    $envContent | Out-File -FilePath ".env" -Encoding UTF8
    Write-Host "✅ .env 파일이 생성되었습니다." -ForegroundColor Green
}

# Gradle Wrapper 확인
if ($IsWindows -or $env:OS -eq "Windows_NT") {
    if (Test-Path "gradlew.bat") {
        Write-Host "✅ Gradle Wrapper 확인됨 (Windows)" -ForegroundColor Green
    } else {
        Write-Host "⚠️  gradlew.bat 파일이 없습니다." -ForegroundColor Yellow
    }
} else {
    if (Test-Path "gradlew") {
        # Unix 계열에서는 실행 권한 설정
        chmod +x ./gradlew 2>$null
        Write-Host "✅ Gradle Wrapper 권한 설정 완료" -ForegroundColor Green
    } else {
        Write-Host "⚠️  gradlew 파일이 없습니다." -ForegroundColor Yellow
    }
}

# build.gradle에 Flyway 설정 확인
$buildGradleContent = Get-Content "build.gradle" -Raw -ErrorAction SilentlyContinue
if ($buildGradleContent -and $buildGradleContent -match "flywaydb") {
    Write-Host "✅ Flyway 의존성 확인됨" -ForegroundColor Green
} else {
    Write-Host "⚠️  build.gradle에 Flyway 의존성을 추가해야 합니다." -ForegroundColor Yellow
    Write-Host "   implementation 'org.flywaydb:flyway-core'" -ForegroundColor White
    Write-Host "   implementation 'org.flywaydb:flyway-database-postgresql'" -ForegroundColor White
}

Write-Host ""
Write-Host "🎉 초기 설정이 완료되었습니다!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 다음 단계:" -ForegroundColor Cyan
Write-Host "1. build.gradle에 Flyway 의존성 확인" -ForegroundColor White

if ($IsWindows -or $env:OS -eq "Windows_NT") {
    Write-Host "2. 개발 환경 시작: scripts\dev-start.bat" -ForegroundColor White
    Write-Host "3. 마스터 환경 시작: scripts\master-start.bat" -ForegroundColor White
    Write-Host "4. DB 초기화: scripts\db-reset.bat" -ForegroundColor White
} else {
    Write-Host "2. 개발 환경 시작: ./scripts/dev-start.sh" -ForegroundColor White
    Write-Host "3. 마스터 환경 시작: ./scripts/master-start.sh" -ForegroundColor White
    Write-Host "4. DB 초기화: ./scripts/db-reset.sh" -ForegroundColor White
}

Write-Host ""
Write-Host "🔧 설정 파일 위치:" -ForegroundColor Cyan
Write-Host "- 환경 변수: .env" -ForegroundColor White
Write-Host "- Docker 설정: docker/" -ForegroundColor White
Write-Host "- 마이그레이션: src/main/resources/db/migration/" -ForegroundColor White

if ($IsWindows -or $env:OS -eq "Windows_NT") {
    Read-Host "Enter 키를 눌러 계속하세요"
}