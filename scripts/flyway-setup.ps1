#!/usr/bin/env pwsh
# Flyway 설정 및 마이그레이션 도우미 스크립트 (web-demo)

#Requires -Version 7.0

param()

$ErrorActionPreference = "Stop"

Write-Host "🔧 web-demo Flyway 설정 도우미" -ForegroundColor Cyan

# 프로젝트 루트 확인
if (-not (Test-Path "build.gradle")) {
    Write-Error "❌ 프로젝트 루트에서 실행해주세요."
}

# 환경 변수 로드
if (Test-Path ".env") {
    Get-Content ".env" | Where-Object {
        $_ -notmatch '^#' -and $_ -notmatch '^\s*$'
    } | ForEach-Object {
        $key, $value = $_ -split '=', 2
        if ($key -and $value) {
            $trimmedKey = $key.Trim()
            $trimmedValue = $value.Trim()
            Set-Item -Path "env:$trimmedKey" -Value $trimmedValue
        }
    }
}

# 마이그레이션 디렉토리 확인 및 생성
$migrationDir = "src/main/resources/db/migration"
if (-not (Test-Path $migrationDir)) {
    New-Item -Path $migrationDir -ItemType Directory -Force | Out-Null
    Write-Host "📁 마이그레이션 디렉토리 생성: $migrationDir" -ForegroundColor Green
}

# 명령어 선택 메뉴
$menu = @"

수행할 작업을 선택하세요:
1. 새 마이그레이션 파일 생성
2. 마이그레이션 실행 (dev)
3. 마이그레이션 실행 (master)
4. 마이그레이션 상태 확인
5. 마이그레이션 히스토리 조회
6. 샘플 테이블 마이그레이션 생성
7. 종료

"@

Write-Host $menu -ForegroundColor Yellow
$choice = Read-Host "선택 (1-7)"

switch ($choice) {
    "1" {
        # 새 마이그레이션 파일 생성
        Write-Host ""
        $filename = Read-Host "마이그레이션 파일명을 입력하세요 (예: Create_user_table)"

        if ([string]::IsNullOrWhiteSpace($filename)) {
            Write-Error "❌ 파일명이 필요합니다."
        }

        # 버전 번호 자동 생성
        $existingFiles = Get-ChildItem -Path $migrationDir -Filter "V*.sql" -ErrorAction SilentlyContinue
        $lastVersion = 0

        if ($existingFiles) {
            $versions = $existingFiles | ForEach-Object {
                if ($_.Name -match 'V(\d+)__') {
                    [int]$matches[1]
                }
            }
            $lastVersion = ($versions | Measure-Object -Maximum).Maximum
        }

        $nextVersion = $lastVersion + 1
        $migrationFile = Join-Path $migrationDir "V${nextVersion}__${filename}.sql"

        $migrationContent = @"
-- V${nextVersion}__${filename}.sql
-- 작성자: $env:USERNAME
-- 작성일: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
-- 설명: ${filename}

-- ========================================
-- ${filename}
-- ========================================

-- 여기에 DDL 작성
-- 예시:
-- CREATE TABLE example_table (
--     id BIGSERIAL PRIMARY KEY,
--     name VARCHAR(100) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- 인덱스 생성
-- CREATE INDEX idx_example_name ON example_table(name);

-- 코멘트 추가
-- COMMENT ON TABLE example_table IS '예시 테이블';

"@

        Set-Content -Path $migrationFile -Value $migrationContent -Encoding UTF8

        Write-Host "✅ 마이그레이션 파일 생성 완료:" -ForegroundColor Green
        Write-Host "   📄 $migrationFile" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "파일을 편집한 후 마이그레이션을 실행하세요." -ForegroundColor Yellow
    }

    "2" {
        # 개발 환경 마이그레이션
        Write-Host "🔄 개발 환경 마이그레이션 실행 중..." -ForegroundColor Blue

        $dbName = $env:DB_NAME ?? "devdb"
        $dbUser = $env:DB_USER ?? "devuser"
        $dbPassword = $env:DB_PASSWORD ?? "devpass"

        $gradleWrapper = if ($IsWindows) { ".\gradlew.bat" } else { "./gradlew" }

        try {
            & $gradleWrapper flywayMigrate `
                "-Dflyway.url=jdbc:postgresql://localhost:5432/$dbName" `
                "-Dflyway.user=$dbUser" `
                "-Dflyway.password=$dbPassword"

            Write-Host "✅ 개발 환경 마이그레이션 완료!" -ForegroundColor Green
        } catch {
            Write-Error "❌ 마이그레이션 실행 실패: $_"
        }
    }

    "3" {
        # 마스터 환경 마이그레이션
        Write-Host "⚠️  마스터 환경에 마이그레이션을 실행합니다." -ForegroundColor Red
        $confirm = Read-Host "계속하시겠습니까? (y/n)"

        if ($confirm -eq "y" -or $confirm -eq "Y") {
            Write-Host "🔄 마스터 환경 마이그레이션 실행 중..." -ForegroundColor Blue

            $masterDbName = $env:MASTER_DB_NAME ?? "web_demo_master"
            $masterDbUser = $env:MASTER_DB_USER ?? "masteruser"
            $masterDbPassword = $env:MASTER_DB_PASSWORD ?? "masterpass"

            $gradleWrapper = if ($IsWindows) { ".\gradlew.bat" } else { "./gradlew" }

            try {
                & $gradleWrapper flywayMigrate `
                    "-Dflyway.url=jdbc:postgresql://localhost:5432/$masterDbName" `
                    "-Dflyway.user=$masterDbUser" `
                    "-Dflyway.password=$masterDbPassword"

                Write-Host "✅ 마스터 환경 마이그레이션 완료!" -ForegroundColor Green
            } catch {
                Write-Error "❌ 마스터 환경 마이그레이션 실패: $_"
            }
        } else {
            Write-Host "❌ 취소되었습니다." -ForegroundColor Yellow
        }
    }

    "4" {
        # 마이그레이션 상태 확인
        Write-Host "📋 마이그레이션 상태 확인 (개발 환경):" -ForegroundColor Cyan

        $dbName = $env:DB_NAME ?? "devdb"
        $dbUser = $env:DB_USER ?? "devuser"
        $dbPassword = $env:DB_PASSWORD ?? "devpass"

        $gradleWrapper = if ($IsWindows) { ".\gradlew.bat" } else { "./gradlew" }

        try {
            & $gradleWrapper flywayInfo `
                "-Dflyway.url=jdbc:postgresql://localhost:5432/$dbName" `
                "-Dflyway.user=$dbUser" `
                "-Dflyway.password=$dbPassword"
        } catch {
            Write-Error "❌ 상태 확인 실패: $_"
        }
    }

    "5" {
        # 마이그레이션 히스토리
        Write-Host "📋 마이그레이션 히스토리 (개발 환경):" -ForegroundColor Cyan

        $dbName = $env:DB_NAME ?? "devdb"
        $dbUser = $env:DB_USER ?? "devuser"

        try {
            $query = "SELECT version, description, installed_on, success FROM flyway_schema_history ORDER BY installed_rank;"
            docker exec web-demo-postgres-dev psql -U $dbUser -d $dbName -c $query
            Write-Host "✅ 히스토리 조회 완료" -ForegroundColor Green
        } catch {
            Write-Host "❌ 히스토리 조회 실패. DB가 실행 중인지 확인하세요." -ForegroundColor Red
        }
    }

    "6" {
        # 샘플 테이블 마이그레이션 생성
        Write-Host "📝 샘플 테이블 마이그레이션 생성 중..." -ForegroundColor Blue

        # 버전 번호 자동 생성
        $existingFiles = Get-ChildItem -Path $migrationDir -Filter "V*.sql" -ErrorAction SilentlyContinue
        $lastVersion = 0

        if ($existingFiles) {
            $versions = $existingFiles | ForEach-Object {
                if ($_.Name -match 'V(\d+)__') {
                    [int]$matches[1]
                }
            }
            $lastVersion = ($versions | Measure-Object -Maximum).Maximum
        }

        $nextVersion = $lastVersion + 1
        $migrationFile = Join-Path $migrationDir "V${nextVersion}__Create_sample_tables.sql"

        $sampleContent = @'
-- V1__Create_sample_tables.sql
-- 샘플 테이블 생성 (web-demo 프로젝트)

-- 회원 유형 ENUM 생성
CREATE TYPE member_type AS ENUM ('USER', 'COMPANY');

CREATE TABLE member
(
    id                  BIGSERIAL PRIMARY KEY,                        -- 내부용 PK (시퀀스 기반)
    member_uuid         VARCHAR(50)  NOT NULL UNIQUE,                 -- 외부 참조용 UUID
    name                VARCHAR(100) NOT NULL,                        -- 이름 (회사 또는 개인)
    phone_number        VARCHAR(30),                                  -- 연락처
    email               VARCHAR(100),                                 -- 이메일
    member_type         member_type NOT NULL,                         -- 회원 유형 (USER / COMPANY)
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,          -- 등록일시
    updated_at          TIMESTAMP                                     -- 수정일시
);

-- 인덱스 생성
CREATE INDEX idx_member_uuid ON member(member_uuid);
CREATE INDEX idx_member_email ON member(email);
CREATE INDEX idx_member_type ON member(member_type);

-- 코멘트 추가
COMMENT ON TABLE member IS '회원 정보 마스터 테이블 (개인/법인 구분 포함)';
COMMENT ON COLUMN member.id IS '시퀀스 기반 내부 식별자';
COMMENT ON COLUMN member.member_uuid IS '외부 참조용 UUID';
COMMENT ON COLUMN member.name IS '회원 이름 또는 회사명';
COMMENT ON COLUMN member.phone_number IS '연락처';
COMMENT ON COLUMN member.email IS '이메일 주소';
COMMENT ON COLUMN member.member_type IS '회원 유형(USER 또는 COMPANY)';
COMMENT ON COLUMN member.created_at IS '생성 시각';
COMMENT ON COLUMN member.updated_at IS '최종 수정 시각';

-- 샘플 데이터 삽입
INSERT INTO member (member_uuid, name, email, member_type) VALUES
('user-001', '김개발', 'kim@example.com', 'USER'),
('company-001', '(주)테크컴퍼니', 'contact@techcompany.com', 'COMPANY');
'@

        Set-Content -Path $migrationFile -Value $sampleContent -Encoding UTF8

        Write-Host "✅ 샘플 테이블 마이그레이션 생성 완료:" -ForegroundColor Green
        Write-Host "   📄 $migrationFile" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "🚀 마이그레이션 실행: .\scripts\flyway-setup.ps1 (옵션 2 선택)" -ForegroundColor Yellow
    }

    "7" {
        Write-Host "👋 스크립트를 종료합니다." -ForegroundColor Green
        exit 0
    }

    default {
        Write-Host "❌ 잘못된 선택입니다." -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "✨ 작업 완료!" -ForegroundColor Green