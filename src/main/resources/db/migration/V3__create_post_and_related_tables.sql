-- V3__create_post_and_related_tables.sql

-- 게시글 테이블
CREATE TABLE post (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_email VARCHAR(255) NOT NULL, -- 이메일로 작성자 구분
    post_type VARCHAR(50) NOT NULL,
    is_secret BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parent_id BIGINT,

    CONSTRAINT fk_post_parent FOREIGN KEY (parent_id) REFERENCES post(id)
);

-- 댓글 테이블
CREATE TABLE comment (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    author_email VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    parent_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comment(id)
);

-- 첨부파일 테이블
CREATE TABLE post_attachment (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attachment_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX idx_post_post_type ON post(post_type);
CREATE INDEX idx_post_author_email ON post(author_email);
CREATE INDEX idx_comment_post_id ON comment(post_id);
CREATE INDEX idx_comment_author_email ON comment(author_email);
CREATE INDEX idx_attachment_post_id ON post_attachment(post_id);
