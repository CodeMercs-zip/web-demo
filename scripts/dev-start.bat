@echo off
setlocal enabledelayedexpansion

REM 개발 환경 시작 스크립트 (web-demo Windows)

echo 🔧 web-demo 개발 환경 시작 중...

REM 프로젝트 루트 확인
if not exist "build.gradle" (
    echo ❌ 프로젝트 루트에서 실행해주세요.
    pause
    exit /b 1
)

REM 환경 변수 로드
if exist ".env" (
    for /f "usebackq tokens=1,2 delims==" %%a in (".env") do (
        if not "%%a"=="" if not "%%a:~0,1%"=="#" (
            set "%%a=%%b"
        )
    )
    echo ✅ 환경 변수 로드됨
)

REM 기존 컨테이너 정리
echo 🧹 기존 개발 컨테이너 정리 중...
cd docker
docker-compose -f docker-compose.dev.yml down -v --remove-orphans
cd ..

REM 개발 환경 시작
echo 🚀 PostgreSQL 개발 환경 시작 중...
cd docker
docker-compose -f docker-compose.dev.yml up -d
cd ..

REM 헬스체크 대기
echo ⏳ PostgreSQL 헬스체크 대기 중...
set /a timeout=60
:healthcheck_loop
docker exec web-demo-postgres-dev pg_isready -U %DB_USER% >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ PostgreSQL 연결 성공!
    goto :healthcheck_done
)
echo .
timeout /t 2 >nul
set /a timeout-=2
if %timeout% gtr 0 goto :healthcheck_loop

echo ❌ PostgreSQL 연결 시간 초과
echo 로그 확인: docker logs web-demo-postgres-dev
pause
exit /b 1

:healthcheck_done

REM Flyway 마이그레이션 확인 및 실행
if exist "src\main\resources\db\migration" (
    dir /b "src\main\resources\db\migration\*.sql" >nul 2>&1
    if !errorlevel! equ 0 (
        echo 🔄 Flyway 마이그레이션 실행 중...
        gradlew.bat flywayMigrate -Dflyway.url=jdbc:postgresql://localhost:5432/%DB_NAME% -Dflyway.user=%DB_USER% -Dflyway.password=%DB_PASSWORD%
        echo ✅ 마이그레이션 완료!
    ) else (
        echo ℹ️  마이그레이션 파일이 없습니다. 필요시 추가하세요.
    )
)

echo.
echo 🎉 개발 환경 준비 완료!
echo.
echo 📊 접속 정보:
echo    🗄️  PostgreSQL: localhost:5432
echo    🌐 Adminer: http://localhost:8080
echo    📋 DB: %DB_NAME%
echo    👤 User: %DB_USER%
echo    🔑 Pass: %DB_PASSWORD%
echo.
echo 🚀 Spring Boot 실행:
echo    gradlew.bat bootRun --args="--spring.profiles.active=dev"
echo.
echo 🔧 유용한 명령어:
echo    - 로그 확인: docker logs web-demo-postgres-dev
echo    - DB 초기화: scripts\db-reset.bat
echo    - 환경 중지: cd docker ^&^& docker-compose -f docker-compose.dev.yml down

pause