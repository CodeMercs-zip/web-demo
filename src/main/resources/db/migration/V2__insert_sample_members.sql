-- V2__insert_sample_members.sql
-- 샘플 회원 데이터 삽입

-- 개인 회원 (USER) 삽입
INSERT INTO member (
    name, 
    phone_number, 
    email, 
    password, 
    member_type, 
    is_deleted, 
    created_at, 
    updated_at
) VALUES (
    '용병단장김라곰',
    '010-7777-7777',
    'kimrakom@example.com',
    '!677tkdgus',
    'USER',
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO member (
    name,
    phone_number,
    email,
    password,
    member_type,
    is_deleted,
    created_at,
    updated_at
) VALUES (
    '그의부하scv',
    '010-7788-7777',
    'scv718@example.com',
    '!677tkdgus',
    'USER',
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);


-- 회사 회원 (COMPANY) 삽입
INSERT INTO member (
    name,
    phone_number,
    email,
    password,
    member_type,
    is_deleted,
    created_at,
    updated_at
) VALUES (
    '(주)개쩌는트리오',
    '02-1234-5678',
    'contact@mercs.company.com',
    'company123',
    'COMPANY',
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
