CREATE TABLE IF NOT EXISTS merchants
(
    id         UUID         NOT NULL PRIMARY KEY,
    api_key    varchar(255) NOT NULL,
    name       varchar(255) NOT NULL,
    env        char(4)      NOT NULL DEFAULT 'test',
    active     boolean      NOT NULL DEFAULT true,
    created_at timestamp    NOT NULL,
    updated_at timestamp    NOT NULL
);

INSERT INTO merchants (id, api_key, name, env, active, created_at, updated_at)
VALUES ('63997f34-66d5-4e49-82a3-065dca2ff149', '$2a$10$e6pt43pOpxNOsB2MYC.9puYTdVV8HRME155Hq99dW8r6IqtV6qz2O',
        'junit', 'test', true, now(), now());

