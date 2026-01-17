-- SQL Script to initialize roles in auth_db
-- Run this script AFTER the application creates the roles table

INSERT INTO roles (name, description) VALUES
('ROLE_CLIENT', 'Klient siłowni - może się zapisywać na zajęcia'),
('ROLE_TRAINER', 'Trener - może dodawać zajęcia'),
('ROLE_ADMIN', 'Administrator - pełny dostęp')
ON CONFLICT (name) DO NOTHING;

-- Verify roles were inserted
SELECT * FROM roles;
