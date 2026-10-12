-- Portfolio snapshot: no seeded application accounts.
ALTER TABLE app_user
    ADD COLUMN password_hash VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'USER';
