CREATE EXTENSION pgcrypto;

CREATE TABLE users
(
    id         UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    email      TEXT      NOT NULL,
    password   TEXT      NOT NULL,
    active     boolean   NOT NULL default true,
    created_at timestamp NOT NULL default CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL default CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX users_email ON users (email);
