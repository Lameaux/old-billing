CREATE TABLE IF NOT EXISTS users
(
    id              UUID         NOT NULL PRIMARY KEY,
    email           varchar(255) NOT NULL,
    password_hash   varchar(255) NOT NULL,
    msisdn          varchar(15),
    name            varchar(255),
    active          boolean      NOT NULL DEFAULT true,
    admin           boolean      NOT NULL DEFAULT false,
    email_verified  boolean      NOT NULL DEFAULT false,
    msisdn_verified boolean      NOT NULL DEFAULT false,
    created_at      timestamp    NOT NULL,
    updated_at      timestamp    NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS users_email_unique_idx ON users (email);
