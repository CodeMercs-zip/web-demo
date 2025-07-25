@echo off
setlocal enabledelayedexpansion

REM 프로젝트 Docker 환경 초기 설정 스크립트
REM web-demo 프로젝트용 (Windows)

echo 🚀 web-demo PostgreSQL Docker 환경 설정 시작...

REM 아키텍처 확인
set ARCH=%PROCESSOR_ARCHITECTURE%
echo 🖥️  운영체제: Windows (%ARCH%)

REM 현재 디렉토리가 프로젝트 루트인지 확인
if not exist "build.gradle" (
    echo ❌ 프로젝트 루트 디렉토리에서 실행해주세요.
    pause
    exit /b 1
)

REM 아키텍처 체크
if "%ARCH%"=="ARM64" (
    echo ✅ ARM64 환경 확인됨 (Windows on ARM)
) else if "%ARCH%"=="AMD64" (
    echo ✅ x86_64 환경 확인됨 (Intel/AMD Windows)
) else (
    echo ⚠️  알 수 없는 아키텍처: %ARCH%. 계속 진행합니다.
)

REM Docker 실행 체크
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker가 실행되지 않았습니다.
    echo    Docker Desktop for Windows를 시작해주세요.
    pause
    exit /b 1
)

REM 필요한 디렉토리 생성
echo 📁 디렉토리 구조 생성 중...
if not exist "docker\init-scripts" mkdir docker\init-scripts
if not exist "docker\test-data" mkdir docker\test-data
if not exist "scripts" mkdir scripts
if not exist "src\main\resources\db\migration" mkdir src\main\resources\db\migration

REM 환경 파일 생성
if not exist ".env" (
    echo 📝 환경 파일 생성 중...
    (
        echo # 개발 환경 설정
        echo DB_NAME=web_demo_dev
        echo DB_USER=devuser
        echo DB_PASSWORD=devpass
        echo.
        echo # 마스터 환경 설정
        echo MASTER_DB_NAME=web_demo_master
        echo MASTER_DB_USER=masteruser
        echo MASTER_DB_PASSWORD=masterpass
        echo.
        echo # Spring 프로파일
        echo SPRING_PROFILES_ACTIVE=dev
        echo.
        echo # Docker Compose 프로젝트명
        echo COMPOSE_PROJECT_NAME=web-demo
    ) > .env
    echo ✅ .env 파일이 생성되었습니다.
)

REM Gradle Wrapper 확인
if exist "gradlew.bat" (
    echo ✅ Gradle Wrapper 확인됨
) else (
    echo ⚠️  gradlew.bat 파일이 없습니다.
)

REM build.gradle에 Flyway 설정 확인
findstr /c:"flywaydb" build.gradle >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  build.gradle에 Flyway 의존성을 추가해야 합니다.
    echo    implementation 'org.flywaydb:flyway-core'
    echo    implementation 'org.flywaydb:flyway-database-postgresql'
)

echo.
echo 🎉 초기 설정이 완료되었습니다!
echo.
echo 📋 다음 단계:
echo 1. build.gradle에 Flyway 의존성 확인
echo 2. 개발 환경 시작: scripts\dev-start.bat
echo 3. 마스터 환경 시작: scripts\master-start.bat
echo 4. DB 초기화: scripts\db-reset.bat
echo.
echo 🔧 설정 파일 위치:
echo - 환경 변수: .env
echo - Docker 설정: docker\
echo - 마이그레이션: src\main\resources\db\migration\

pause