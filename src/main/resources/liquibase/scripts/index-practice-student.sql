-- liquibase formatted sql

-- changeset VioletNeon:1
CREATE INDEX student_name_index ON student (name);