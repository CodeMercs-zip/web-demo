-- V5__create_inquiries_table.sql
-- 문의(Inquiry) 테이블 생성

CREATE TABLE inquiries
(
    id           BIGSERIAL PRIMARY KEY,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    subject      VARCHAR(200) NOT NULL,
    message      TEXT         NOT NULL,
    phone_number VARCHAR(20),
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
