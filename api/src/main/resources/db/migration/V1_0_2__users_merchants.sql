CREATE TABLE IF NOT EXISTS users_merchants
(
    id          UUID        NOT NULL PRIMARY KEY,
    user_id     UUID        NOT NULL,
    merchant_id UUID        NOT NULL,
    role        varchar(15) NOT NULL,
    created_at  timestamp   NOT NULL,
    updated_at  timestamp   NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS users_merchants_unique_idx ON users_merchants (user_id, merchant_id);
