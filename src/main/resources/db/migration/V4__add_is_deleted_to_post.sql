-- V4__create_post_and_related_tables.sql
-- post soft delete colunm 추가

ALTER TABLE post ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
CREATE INDEX idx_post_is_deleted ON post(is_deleted);
