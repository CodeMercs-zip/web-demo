-- V1__init_member_schema.sql
-- 회원 테이블 정의

-- enum 선언
-- member_type
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'member_type') THEN
        CREATE TYPE member_type AS ENUM (
            'USER',
            'COMPANY'
        );
    END IF;
END $$;

-- member 테이블
-- 사용자 및 회사 정보를 통합 관리하는 마스터 회원 테이블
CREATE TABLE member
(
    id                  BIGSERIAL PRIMARY KEY,                                              -- 내부용 PK (시퀀스 기반)
    name                VARCHAR(100) NOT NULL,                                              -- 이름 (회사 또는 개인)
    phone_number        VARCHAR(30),                                                        -- 연락처
    email               VARCHAR(100),                                                       -- 이메일
    password            VARCHAR(255) NOT NULL,                                              -- 비밀번호
    member_type         member_type NOT NULL,                                               -- 회원 유형 (USER / COMPANY)
    is_deleted          BOOLEAN NOT NULL DEFAULT FALSE,                                     -- 탈퇴 여부 (TRUE: 탈퇴, FALSE: 정상)
    deleted_at          TIMESTAMP NULL,                                                     -- 탈퇴 일시 (NULL: 정상 회원)
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                                -- 등록일시
    updated_at          TIMESTAMP                                                           -- 수정일시
);

-- 인덱스 추가 (성능 최적화)
CREATE INDEX idx_member_is_deleted ON member(is_deleted);
CREATE INDEX idx_member_deleted_at ON member(deleted_at);

COMMENT ON TABLE member IS '회원 정보 마스터 테이블';
COMMENT ON COLUMN member.id IS '시퀀스 기반 내부 식별자';
COMMENT ON COLUMN member.name IS '회원 이름 또는 회사명';
COMMENT ON COLUMN member.phone_number IS '연락처';
COMMENT ON COLUMN member.email IS '이메일 주소';
COMMENT ON COLUMN member.password IS '비밀번호';
COMMENT ON COLUMN member.member_type IS '회원 유형(USER 또는 COMPANY)';
COMMENT ON COLUMN member.is_deleted IS '탈퇴 여부 (TRUE: 탈퇴, FALSE: 정상)';
COMMENT ON COLUMN member.deleted_at IS '탈퇴 일시 (NULL: 정상 회원)';
COMMENT ON COLUMN member.created_at IS '생성 시각';
COMMENT ON COLUMN member.updated_at IS '최종 수정 시각';
