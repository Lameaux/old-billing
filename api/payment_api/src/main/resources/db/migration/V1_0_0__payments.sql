CREATE TABLE IF NOT EXISTS payments
(
    id          UUID DEFAULT RANDOM_UUID() NOT NULL PRIMARY KEY,
    merchant_id UUID                       NOT NULL
);

CREATE INDEX IF NOT EXISTS payments_merchant_id_idx ON payments (merchant_id);
