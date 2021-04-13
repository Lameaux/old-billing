CREATE TABLE IF NOT EXISTS payments
(
    id                 UUID        NOT NULL PRIMARY KEY,
    merchant_id        UUID        NOT NULL,
    customer_id        UUID,
    merchant_reference varchar(255),
    state              varchar(10) NOT NULL,
    created_at         timestamp   NOT NULL,
    updated_at         timestamp   NOT NULL
);

CREATE INDEX IF NOT EXISTS payments_merchant_customer_idx ON payments (merchant_id, customer_id);

CREATE UNIQUE INDEX IF NOT EXISTS payments_merchant_unique_idx ON payments (merchant_id, merchant_reference);


