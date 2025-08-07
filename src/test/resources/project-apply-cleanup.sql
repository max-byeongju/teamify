-- study-apply-cleanup.sql

-- 외래 키 제약조건 때문에 참조하는 테이블부터 삭제
-- TRUNCATE는 DELETE보다 빠르고, auto-increment 값도 초기화
TRUNCATE TABLE project_application;
TRUNCATE TABLE project;
TRUNCATE TABLE member;