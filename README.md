# Web Demo

Spring Boot 3.4.2 기반의 웹 애플리케이션입니다. JWT 인증, PostgreSQL 데이터베이스, 멀티 환경 지원을 제공합니다.

## 🚀 빠른 시작

### 요구사항

- Java 21
- Docker & Docker Compose
- PowerShell 7.0+ (Windows) 또는 Bash (macOS/Linux)

### 개발 환경 시작

**Windows (PowerShell):**

```powershell
# Docker 환경만 시작
.\scripts\dev-run.ps1

# 완전 초기화 + DB 마이그레이션
.\scripts\dev-start.ps1

# 환경 종료
.\scripts\dev-stop.ps1
```

**macOS/Linux (Bash):**

```bash
# 스크립트 실행 권한 설정 (최초 1회)
chmod +x scripts/*.sh

# Docker 환경만 시작  
./scripts/dev-run.sh

# 완전 초기화 + DB 마이그레이션
./scripts/dev-start.sh

# 환경 종료
./scripts/dev-stop.sh
```

### Spring Boot 애플리케이션 실행

```bash
# 개발 모드
./gradlew bootRun --args='--spring.profiles.active=dev'

# 운영 모드 (별도 운영 DB 사용 - 현재 주석 처리됨)
# ./gradlew bootRun --args='--spring.profiles.active=master'
```

## 📋 주요 기능

- **JWT 기반 인증**: Access Token (1시간), Refresh Token (30일)
- **OAuth2 소셜 로그인**: Google, Kakao, Naver 지원
- **회원 관리**: 개인/법인 회원 지원, 소프트 삭제
- **멀티 환경**: dev 프로파일 (master는 별도 운영 DB용으로 주석 처리됨)
- **데이터베이스**: PostgreSQL + Flyway 마이그레이션
- **API 로깅**: AOP 기반 요청/응답 로깅
- **보안**: Spring Security + CORS 설정

## 🗄️ 데이터베이스 정보

**개발 환경 (dev):**

- DB: devdb / devuser / devpass
- 포트: 5432
- 특징: 컨테이너 재시작 시 데이터 초기화

**운영 환경 (현재 주석 처리됨):**

- 별도 운영 DB를 사용하므로 master 프로파일 설정이 주석 처리되어 있습니다

**Adminer 웹 UI:**

- URL: http://localhost:8080
- 자동 로그인: http://localhost:8080/?pgsql=postgres-dev&username=devuser&password=devpass&db=devdb

## 🛠️ Flyway 마이그레이션

```bash
./gradlew flywayMigrate    # 마이그레이션 실행
./gradlew flywayInfo       # 상태 확인  
./gradlew flywayClean      # 데이터베이스 정리 (주의!)
./gradlew flywayValidate   # 마이그레이션 검증
```

## 🌐 서비스 포트

- **Spring Boot**: http://localhost:17070
- **PostgreSQL**: localhost:5432
- **Adminer**: http://localhost:8080
- **Swagger UI**: http://localhost:17070/swagger-ui/index.html
- **OpenAPI Docs**: http://localhost:17070/v3/api-docs

## 📁 프로젝트 구조

```
web-demo/
├── docker/
│   └── docker-compose.dev.yml      # 개발 환경 설정
├── scripts/
│   ├── dev-run.ps1/.sh             # Docker 환경만 시작
│   ├── dev-stop.ps1/.sh            # Docker 환경 종료  
│   ├── dev-start.ps1/.sh           # 완전 초기화 + DB 마이그레이션
│   └── setup-project.sh            # 프로젝트 초기 설정 (환경 파일, 권한 설정)
├── src/main/
│   ├── java/com/rgs/web_demo/
│   │   ├── controller/              # REST 컨트롤러
│   │   ├── service/                 # 비즈니스 로직
│   │   ├── domain/                  # JPA 엔티티
│   │   ├── dto/                     # 데이터 전송 객체
│   │   ├── config/                  # 설정 클래스
│   │   └── aop/                     # AOP 로깅
│   └── resources/
│       ├── application.yml          # 공통 설정
│       ├── application-dev.yml      # 개발 환경 설정
│       └── db/migration/            # Flyway 마이그레이션
├── test-requests/
│   └── member-create.http           # API 테스트 (curl 명령어)
└── CLAUDE.md                        # 개발 가이드라인
```

## 🔐 OAuth2 소셜 로그인

### 지원 제공자

- **Google OAuth2**: 사용자 프로필 및 이메일 접근
- **Kakao OAuth2**: 프로필 및 이메일 접근 (비즈앱 승인 필요)
- **Naver OAuth2**: 프로필 및 이메일 접근

### 로그인 URL

- **Google**: `http://localhost:17070/oauth2/authorization/google`
- **Kakao**: `http://localhost:17070/oauth2/authorization/kakao`
- **Naver**: `http://localhost:17070/oauth2/authorization/naver`

### 로그인 플로우

1. 소셜 로그인 URL 접속
2. OAuth 제공자에서 인증 진행
3. 인증 성공 시 애플리케이션으로 리다이렉트
4. JWT 토큰 (access + refresh) 자동 생성
5. 프론트엔드 콜백으로 리다이렉트: `http://localhost:3000/oauth/callback?accessToken=...&refreshToken=...`

## 🧪 API 테스트

### 공개 API (인증 불필요)

```bash
# 공개 테스트 엔드포인트
curl -X GET http://localhost:17070/api/test/public

# 회원 등록
curl -X POST http://localhost:17070/api/v1/member \
  -H "Content-Type: application/json" \
  -d '{"name":"김개발","email":"kim@example.com","password":"password123","memberType":"USER"}'

# 회원 조회
curl -X GET http://localhost:17070/api/v1/member/1

# 회원 목록
curl -X GET "http://localhost:17070/api/v1/member?page=0&size=10"
```

### 인증 필요 API (JWT 토큰 필요)

```bash
# 보호된 엔드포인트 (사용자 정보 반환)
curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     http://localhost:17070/api/test/protected

# 토큰 정보 조회
curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     http://localhost:17070/api/test/token-info

# 토큰 유효성 검사
curl -X POST http://localhost:17070/api/test/validate-token \
     -H "Content-Type: application/json" \
     -d '{"token": "YOUR_ACCESS_TOKEN"}'
```

### PowerShell 사용 시

```powershell
# 보호된 API 호출
Invoke-RestMethod -Uri "http://localhost:17070/api/test/protected" -Headers @{ "Authorization" = "Bearer YOUR_ACCESS_TOKEN" }
```

## 🔧 개발 도구

- **Lombok**: 코드 간소화
- **QueryDSL**: 타입 안전한 쿼리
- **Swagger**: API 문서화 (개발 중)
- **AOP**: 컨트롤러 로깅
- **Flyway**: 데이터베이스 마이그레이션

## 🔧 프로젝트 초기 설정

**setup-project.sh**는 새로운 환경에서 프로젝트를 처음 설정할 때 사용하는 스크립트입니다:

### 📋 수행하는 작업들:

1. **환경 확인**: Docker 실행 상태, 시스템 아키텍처 체크
2. **디렉토리 생성**: 필요한 프로젝트 디렉토리 구조 생성
3. **환경 파일 생성**: `.env` 파일 자동 생성 (개발 환경 설정만, 운영 DB는 TODO로 주석 처리)
4. **권한 설정**: Gradle Wrapper와 스크립트 파일들 실행 권한 부여
5. **의존성 확인**: Flyway 등 필수 의존성 설정 여부 확인

### 🎯 사용 시점:

- 프로젝트를 새로운 환경에 클론했을 때
- `.env` 파일이 없거나 권한 문제가 있을 때
- 프로젝트 구조를 재설정해야 할 때

```bash
# 사용법 (macOS/Linux)
./scripts/setup-project.sh
```

## 📝 참고사항

- JWT secret과 DB 자격증명은 환경별로 다르게 설정됩니다
- 모든 타임스탬프는 Asia/Seoul 타임존을 사용합니다
- Member 테이블은 개인(USER)과 법인(COMPANY) 모두 지원합니다
- 소프트 삭제를 지원하여 데이터 복구가 가능합니다
- Swagger UI를 통해 API 문서화 및 테스트가 가능합니다