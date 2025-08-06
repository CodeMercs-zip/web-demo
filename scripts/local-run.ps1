#!/usr/bin/env pwsh
# LOCAL 환경 Docker 실행 스크립트 - PostgreSQL 컨테이너만 시작
# 로컬 개발용으로 DB 초기화 작업은 별도 스크립트에서 수행

#Requires -Version 7.0

param()

# 오류 발생 시 중단
$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

Write-Host "LOCAL 환경 Docker 시작..."

# 프로젝트 루트 확인
if (-not (Test-Path "build.gradle"))
{
    Write-Error "X 프로젝트 루트에서 실행해주세요."
}

# 환경 변수 로드
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
    Write-Host "✓ 환경 변수 로드됨"
}

# 기존 컨테이너 정리
Write-Host "기존 컨테이너 정리 중..."

# Docker Compose로 정리
Push-Location docker -ErrorAction SilentlyContinue
if ($?)
{
    try
    {
        docker-compose -f docker-compose.local.yml down --remove-orphans 2> $null | Out-Null
    }
    catch
    {
        Write-Host "   기존 Docker Compose 정리 중 오류 (계속 진행)"
    }

    # Docker 환경 시작
    Write-Host "PostgreSQL Docker 환경 시작 중..."
    docker-compose -f docker-compose.local.yml up -d
    Pop-Location
}
else
{
    Write-Error "X docker 폴더를 찾을 수 없습니다."
}

# PostgreSQL 준비 대기
Write-Host "PostgreSQL 연결 대기 중..."

$dbUser = $env:DB_USER ?? "localuser"
$maxAttempts = 30
$attempt = 0
$connected = $false

do
{
    try
    {
        $result = docker exec web-demo-postgres-local pg_isready -U $dbUser 2> $null
        if ($LASTEXITCODE -eq 0)
        {
            Write-Host "`n✓ PostgreSQL 연결 성공!"
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
    Write-Error "`nX PostgreSQL 연결 시간 초과 (${maxAttempts} attempts)"
}

Write-Host ""
Write-Host "✓ LOCAL 환경 Docker 준비 완료!"
Write-Host "Adminer: http://localhost:8080/?pgsql=postgres-local&username=localuser&password=localpass&db=localdb"
Write-Host "이제 IDE에서 Spring Boot를 실행하세요 (--spring.profiles.active=local)"