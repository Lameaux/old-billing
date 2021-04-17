CREATE TABLE IF NOT EXISTS customers
(
    id                 UUID      NOT NULL PRIMARY KEY,
    merchant_id        UUID      NOT NULL,
    merchant_reference varchar(255),
    email              varchar(255),
    name               varchar(255),
    msisdn             varchar(15),
    created_at         timestamp NOT NULL,
    updated_at         timestamp NOT NULL
);

CREATE INDEX IF NOT EXISTS customers_merchant_idx ON customers (merchant_id);

CREATE UNIQUE INDEX IF NOT EXISTS customers_merchant_unique_idx ON customers (merchant_id, merchant_reference);
