-- 개발 환경용 테스트 데이터
-- 이 파일은 수동으로 실행

\echo '=== 테스트 데이터 삽입 시작 ==='

-- 해당 테이블이 존재 할 경우 ( Flyway 마이그레이션 실행 후 사용 )


-- 예시 테스트 데이터
/*
INSERT INTO users (name, email, created_at) VALUES
('개발자1', 'dev1@webdemo.com', CURRENT_TIMESTAMP),
('개발자2', 'dev2@webdemo.com', CURRENT_TIMESTAMP),
('테스터1', 'test1@webdemo.com', CURRENT_TIMESTAMP);

INSERT INTO roles (name, description) VALUES
('ADMIN', '관리자'),
('USER', '일반 사용자'),
('GUEST', '게스트');
*/

\echo '=== 테스트 데이터 준비 완료 ==='
\echo '실제 데이터는 Flyway 마이그레이션 후 추가하세요.'