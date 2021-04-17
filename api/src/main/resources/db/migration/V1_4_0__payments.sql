CREATE TABLE IF NOT EXISTS payments
(
    id                 UUID        NOT NULL PRIMARY KEY,
    merchant_id        UUID        NOT NULL,
    customer_id        UUID,
    state              varchar(15) NOT NULL,
    merchant_reference varchar(255),
    description        varchar(255),
    currency           char(3)     NOT NULL,
    net_amount         decimal     NOT NULL default 0,
    vat_amount         decimal     NOT NULL default 0,
    vat_rate           decimal     NOT NULL default 0,
    total_amount       decimal     NOT NULL,
    instrument_id      UUID,
    provider_reference varchar(255),
    callback_url       varchar(255),
    created_at         timestamp   NOT NULL,
    updated_at         timestamp   NOT NULL
);

CREATE INDEX IF NOT EXISTS payments_merchant_customer_idx ON payments (merchant_id, customer_id);

CREATE UNIQUE INDEX IF NOT EXISTS payments_merchant_unique_idx ON payments (merchant_id, merchant_reference);


