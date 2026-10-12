-- V3__add_user_test_termine.sql

-- Gemeinsamer Termin für den "testuser" (ID: 11111111-1111-1111-1111-111111111111)
INSERT INTO user_verfuegbare_termine (user_id, verfuegbare_termine)
VALUES ('11111111-1111-1111-1111-111111111111', '2026-07-01 14:00:00');

-- Exakt derselbe Termin für den "admin" (ID: 22222222-2222-2222-2222-222222222222)
INSERT INTO user_verfuegbare_termine (user_id, verfuegbare_termine)
VALUES ('22222222-2222-2222-2222-222222222222', '2026-07-01 14:00:00');