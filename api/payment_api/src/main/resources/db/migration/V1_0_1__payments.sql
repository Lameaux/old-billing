CREATE TABLE IF NOT EXISTS payments
(
    id          UUID DEFAULT RANDOM_UUID() NOT NULL PRIMARY KEY,
    merchant_id UUID                       NOT NULL,
    state       VARCHAR(10)                NOT NULL,
    created_at  timestamp                  NOT NULL,
    updated_at  timestamp                  NOT NULL
);

CREATE INDEX IF NOT EXISTS payments_merchant_id_idx ON payments (merchant_id);
