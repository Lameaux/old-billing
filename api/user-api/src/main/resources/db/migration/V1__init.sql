CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    active     boolean   NOT NULL default true,
    internal   boolean   NOT NULL default false,
    created_at timestamp NOT NULL default CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL default CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX users_email ON users (email);
