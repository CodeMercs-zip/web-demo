## 환경 설정

Spring Boot: 17070 포트 
Adminer: 8080 포트     ( http://localhost:8080/ )
PostgreSQL: 5432 포트  

### Windows 사용자
- PowerShell: `powershell -ExecutionPolicy Bypass -File setup.ps1`
- 배치파일: `setup.bat`

### Mac/Linux 사용자 명령어
chmod +x scripts/*.sh
./scripts/setup.sh

# 개발 환경 시작 (PostgreSQL + Adminer)
./scripts/run-dev.sh

# 환경 완전 종료 (컨테이너 삭제)
./scripts/stop-dev.sh

# 환경 재시작
./scripts/restart-dev.sh


# 개발 모드
./gradlew bootRun --args='--spring.profiles.active=dev'

# 마스터 모드
./gradlew bootRun --args='--spring.profiles.active=master'


# 마이그레이션 메뉴 (대화형)
./scripts/flyway-setup.sh

# 직접 명령어
./gradlew flywayMigrate    # 마이그레이션 실행
./gradlew flywayInfo       # 상태 확인
./gradlew flywayClean      # 데이터베이스 정리 (주의!)
./gradlew flywayValidate   # 마이그레이션 검증

backend/
├── docker/
│   ├── docker-compose.dev.yml      # 개발 환경 (데이터 초기화)
│   ├── docker-compose.master.yml   # 마스터 환경 (데이터 영속화)
│   ├── init-scripts/               # 초기 DB 설정
│   └── test-data/                  # 테스트 데이터
├── scripts/
│   ├── setup.sh                    # 초기 설정
│   ├── run-dev.sh                  # 개발 환경 시작
│   ├── stop-dev.sh                 # 환경 종료
│   ├── restart-dev.sh              # 환경 재시작
│   └── flyway-setup.sh             # 마이그레이션 도우미
├── src/main/resources/
│   ├── application.yml             # 공통 설정
│   ├── application-dev.yml         # 개발 환경 설정
│   ├── application-master.yml      # 마스터 환경 설정
│   └── db/migration/               # Flyway 마이그레이션 파일
│       └── V1__init_member_schema.sql
├── .env                         # 환경 변수
└── build.gradle                 # 의존성 및 Flyway 설정

🗄️ 데이터베이스 정보

개발 환경 (dev)
DB명: devdb
사용자: devuser
비밀번호: devpass
특징: 컨테이너 재시작 시 데이터 초기화

마스터 환경 (master)
DB명: masterdb
사용자: masteruser
비밀번호: masterpass
특징: 데이터 영속화 (Docker Volume 사용)