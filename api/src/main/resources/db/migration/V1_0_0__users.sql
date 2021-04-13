CREATE TABLE IF NOT EXISTS users
(
    id            UUID         NOT NULL PRIMARY KEY,
    email         varchar(255) NOT NULL,
    password_hash varchar(255) NOT NULL,
    msisdn        varchar(15),
    name          varchar(255),
    active        boolean      NOT NULL DEFAULT true,
    admin         boolean      NOT NULL DEFAULT false,
    created_at    timestamp    NOT NULL,
    updated_at    timestamp    NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS users_email_unique_idx ON users (email);

INSERT INTO users (id, email, password_hash, name, active, admin, created_at, updated_at)
VALUES ('48c06cf7-debe-4b8d-bc60-eaad520471ef', 'payments@euromoby.com',
        '$2a$10$e6pt43pOpxNOsB2MYC.9puYTdVV8HRME155Hq99dW8r6IqtV6qz2O',
        'euromoby', true, true, now(), now());

INSERT INTO users (id, email, password_hash, name, active, admin, created_at, updated_at)
VALUES ('fe078b12-1d0e-46f7-8089-2f20345e589d', 'payments@example.com',
        '$2a$10$e6pt43pOpxNOsB2MYC.9puYTdVV8HRME155Hq99dW8r6IqtV6qz2O',
        'example', true, false, now(), now());

