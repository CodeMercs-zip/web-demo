#!/usr/bin/env pwsh
# 개발 환경 통합 실행 스크립트 (PowerShell 7.5 버전)

#Requires -Version 7.0

param()

# 오류 발생 시 중단
$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

Write-Host "🚀 web-demo 통합 개발 환경 시작..." -ForegroundColor Green

# 프로젝트 루트 확인
if (-not (Test-Path "build.gradle"))
{
    Write-Error "❌ 프로젝트 루트에서 실행해주세요."
}

# 환경 변수 로드 (수정된 부분)
if (Test-Path ".env")
{
    Get-Content ".env" | Where-Object {
        $_ -notmatch '^#' -and $_ -notmatch '^\s*$'
    } | ForEach-Object {
        $key, $value = $_ -split '=', 2
        if ($key -and $value)
        {
            $trimmedKey = $key.Trim()
            $trimmedValue = $value.Trim()
            Set-Item -Path "env:$trimmedKey" -Value $trimmedValue
        }
    }
    Write-Host "✅ 환경 변수 로드됨" -ForegroundColor Green
}

# 기존 컨테이너 정리
Write-Host "🧹 기존 컨테이너 정리 중..." -ForegroundColor Yellow

# Docker Compose로도 정리
Push-Location docker -ErrorAction SilentlyContinue
if ($?)
{
    try
    {
        docker-compose -f docker-compose.dev.yml down --remove-orphans 2> $null | Out-Null
    }
    catch
    {
        Write-Host "   ⚠️ 기존 Docker Compose 정리 중 오류 (계속 진행)" -ForegroundColor Yellow
    }

    # Docker 환경 시작
    Write-Host "🐳 PostgreSQL Docker 환경 시작 중..." -ForegroundColor Blue
    docker-compose -f docker-compose.dev.yml up -d
    Pop-Location
}
else
{
    Write-Error "❌ docker 폴더를 찾을 수 없습니다."
}

# PostgreSQL 준비 대기
Write-Host "⏳ PostgreSQL 연결 대기 중..." -ForegroundColor Yellow

$dbUser = $env:DB_USER ?? "devuser"
$maxAttempts = 30
$attempt = 0
$connected = $false

do
{
    try
    {
        $result = docker exec web-demo-postgres-dev pg_isready -U $dbUser 2> $null
        if ($LASTEXITCODE -eq 0)
        {
            Write-Host "`n✅ PostgreSQL 연결 성공!" -ForegroundColor Green
            $connected = $true
            break
        }
    }
    catch
    {
        # 연결 실패 시 계속 대기
    }

    Write-Host "." -NoNewline
    Start-Sleep 2
    $attempt++
} while ($attempt -lt $maxAttempts)

if (-not $connected)
{
    Write-Error "`n❌ PostgreSQL 연결 시간 초과 (${maxAttempts} attempts)"
}

Write-Host ""
Write-Host "✅ Docker 환경 준비 완료!" -ForegroundColor Green
Write-Host "🌐 Adminer: http://localhost:8080/?pgsql=postgres-dev&username=devuser&password=devpass&db=devdb" -ForegroundColor Cyan
Write-Host "💡 이제 IDE에서 Spring Boot 실행하세요!" -ForegroundColor Yellow