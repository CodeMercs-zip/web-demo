-- PostgreSQL 초기 설정 스크립트
-- 이 파일은 PostgreSQL 컨테이너 첫 시작 시 자동으로 실행됩니다.

-- 로그 출력
\echo '=== 데이터베이스 초기 설정 시작 ==='

-- 필수 확장 프로그램 설치
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 시간대 설정
SET timezone = 'Asia/Seoul';


-- 기본 함수 생성 (업데이트 시간 자동 갱신)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- 성공 메시지
\echo '=== 데이터베이스 초기 설정 완료 ==='
\echo '확장 프로그램: uuid-ossp, pgcrypto'
\echo '시간대: Asia/Seoul'
\echo '업데이트 트리거 함수: update_updated_at_column() 생성됨'