CREATE TABLE IF NOT EXISTS merchants
(
    id         UUID DEFAULT RANDOM_UUID() NOT NULL PRIMARY KEY,
    env        char(4)                    NOT NULL,
    secret     varchar(255)               NOT NULL,
    name       varchar(255)               NOT NULL,
    active     boolean                    NOT NULL,
    created_at timestamp                  NOT NULL,
    updated_at timestamp                  NOT NULL
);

INSERT INTO merchants (id, env, secret, name, active, created_at, updated_at)
VALUES ('63997f34-66d5-4e49-82a3-065dca2ff149', 'test', '065dca2ff149', 'junit', true, now(), now());

