CREATE TABLE IF NOT EXISTS merchants
(
    id          UUID         NOT NULL PRIMARY KEY,
    name        varchar(255) NOT NULL,
    api_key     varchar(255) NOT NULL,
    description varchar(255),
    env         char(4)      NOT NULL DEFAULT 'TEST',
    active      boolean      NOT NULL DEFAULT true,
    created_at  timestamp    NOT NULL,
    updated_at  timestamp    NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS merchants_name_unique_idx ON merchants (name);
