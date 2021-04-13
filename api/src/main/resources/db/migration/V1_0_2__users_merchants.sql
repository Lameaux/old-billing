CREATE TABLE IF NOT EXISTS users_merchants
(
    user_id     UUID        NOT NULL,
    merchant_id UUID        NOT NULL,
    user_role   varchar(15) NOT NULL,
    created_at  timestamp   NOT NULL,
    updated_at  timestamp   NOT NULL,
    CONSTRAINT pk_users_merchants PRIMARY KEY (user_id, merchant_id)
);

INSERT INTO users_merchants (user_id, merchant_id, user_role, created_at, updated_at)
VALUES ('fe078b12-1d0e-46f7-8089-2f20345e589d', '63997f34-66d5-4e49-82a3-065dca2ff149', 'ADMIN', now(), now());


