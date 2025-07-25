#!/bin/bash

# Flyway 설정 및 마이그레이션 도우미 스크립트 (web-demo)

set -e

echo "🔧 web-demo Flyway 설정 도우미"

# 프로젝트 루트 확인
if [ ! -f "build.gradle" ]; then
    echo "❌ 프로젝트 루트에서 실행해주세요."
    exit 1
fi

# 환경 변수 로드
if [ -f .env ]; then
    export $(cat .env | grep -v '#' | grep -v '^$' | xargs)
fi

# 마이그레이션 디렉토리 확인 및 생성
MIGRATION_DIR="src/main/resources/db/migration"
if [ ! -d "$MIGRATION_DIR" ]; then
    mkdir -p "$MIGRATION_DIR"
    echo "📁 마이그레이션 디렉토리 생성: $MIGRATION_DIR"
fi

# 명령어 선택
echo ""
echo "수행할 작업을 선택하세요:"
echo "1. 새 마이그레이션 파일 생성"
echo "2. 마이그레이션 실행 (dev)"
echo "3. 마이그레이션 실행 (master)"
echo "4. 마이그레이션 상태 확인"
echo "5. 마이그레이션 히스토리 조회"
echo "6. 샘플 테이블 마이그레이션 생성"
read -p "선택 (1-6): " -n 1 -r
echo

case $REPLY in
    1)
        # 새 마이그레이션 파일 생성
        echo ""
        read -p "마이그레이션 파일명을 입력하세요 (예: Create_user_table): " filename
        if [ -z "$filename" ]; then
            echo "❌ 파일명이 필요합니다."
            exit 1
        fi

        # 버전 번호 자동 생성
        last_version=$(ls $MIGRATION_DIR/V*.sql 2>/dev/null | \
                      sed 's/.*V\([0-9]*\)__.*/\1/' | \
                      sort -n | tail -1)
        next_version=$((${last_version:-0} + 1))

        migration_file="$MIGRATION_DIR/V${next_version}__${filename}.sql"

        cat > "$migration_file" << EOF
-- V${next_version}__${filename}.sql
-- 작성자: $(whoami)
-- 작성일: $(date +"%Y-%m-%d %H:%M:%S")
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

EOF

        echo "✅ 마이그레이션 파일 생성 완료:"
        echo "   📄 $migration_file"
        echo ""
        echo "파일을 편집한 후 마이그레이션을 실행하세요."
        ;;

    2)
        # 개발 환경 마이그레이션
        echo "🔄 개발 환경 마이그레이션 실행 중..."
        ./gradlew flywayMigrate \
            -Dflyway.url=jdbc:postgresql://localhost:5432/${DB_NAME:-web_demo_dev} \
            -Dflyway.user=${DB_USER:-devuser} \
            -Dflyway.password=${DB_PASSWORD:-devpass}
        echo "✅ 개발 환경 마이그레이션 완료!"
        ;;

    3)
        # 마스터 환경 마이그레이션
        echo "⚠️  마스터 환경에 마이그레이션을 실행합니다."
        read -p "계속하시겠습니까? (y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo "🔄 마스터 환경 마이그레이션 실행 중..."
            ./gradlew flywayMigrate \
                -Dflyway.url=jdbc:postgresql://localhost:5432/${MASTER_DB_NAME:-web_demo_master} \
                -Dflyway.user=${MASTER_DB_USER:-masteruser} \
                -Dflyway.password=${MASTER_DB_PASSWORD:-masterpass}
            echo "✅ 마스터 환경 마이그레이션 완료!"
        else
            echo "❌ 취소되었습니다."
        fi
        ;;

    4)
        # 마이그레이션 상태 확인
        echo "📋 마이그레이션 상태 확인 (개발 환경):"
        ./gradlew flywayInfo \
            -Dflyway.url=jdbc:postgresql://localhost:5432/${DB_NAME:-web_demo_dev} \
            -Dflyway.user=${DB_USER:-devuser} \
            -Dflyway.password=${DB_PASSWORD:-devpass}
        ;;

    5)
        # 마이그레이션 히스토리
        echo "📋 마이그레이션 히스토리 (개발 환경):"
        if docker exec web-demo-postgres-dev psql -U ${DB_USER:-devuser} -d ${DB_NAME:-web_demo_dev} \
            -c "SELECT version, description, installed_on, success FROM flyway_schema_history ORDER BY installed_rank;" 2>/dev/null; then
            echo "✅ 히스토리 조회 완료"
        else
            echo "❌ 히스토리 조회 실패. DB가 실행 중인지 확인하세요."
        fi
        ;;

    6)
        # 샘플 테이블 마이그레이션 생성
        echo "📝 샘플 테이블 마이그레이션 생성 중..."

        # 버전 번호 자동 생성
        last_version=$(ls $MIGRATION_DIR/V*.sql 2>/dev/null | \
                      sed 's/.*V\([0-9]*\)__.*/\1/' | \
                      sort -n | tail -1)
        next_version=$((${last_version:-0} + 1))

        migration_file="$MIGRATION_DIR/V${next_version}__Create_sample_tables.sql"

        cat > "$migration_file" << 'EOF'
-- V1__Create_sample_tables.sql
-- 샘플 테이블 생성 (web-demo 프로젝트)

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

COMMENT ON TABLE member IS '회원 정보 마스터 테이블 (개인/법인 구분 포함)';
COMMENT ON COLUMN member.id IS '시퀀스 기반 내부 식별자';
COMMENT ON COLUMN member.member_uuid IS '외부 참조용 UUID';
COMMENT ON COLUMN member.name IS '회원 이름 또는 회사명';
COMMENT ON COLUMN member.phone_number IS '연락처';
COMMENT ON COLUMN member.email IS '이메일 주소';
COMMENT ON COLUMN member.member_type IS '회원 유형(USER 또는 COMPANY)';
COMMENT ON COLUMN member.created_at IS '생성 시각';
COMMENT ON COLUMN member.updated_at IS '최종 수정 시각';

EOF

        echo "✅ 샘플 테이블 마이그레이션 생성 완료:"
        echo "   📄 $migration_file"
        echo ""
        echo "🚀 마이그레이션 실행: ./scripts/flyway-setup.sh (옵션 2 선택)"
        ;;

    *)
        echo "❌ 잘못된 선택입니다."
        exit 1
        ;;
esac