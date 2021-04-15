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
