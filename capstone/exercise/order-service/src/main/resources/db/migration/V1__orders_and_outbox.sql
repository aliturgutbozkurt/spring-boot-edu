CREATE TABLE orders (
    id          UUID PRIMARY KEY,                     -- also the reservation key in the catalog
    customer_id VARCHAR(100)   NOT NULL,              -- the "sub" of the customer's JWT
    total       NUMERIC(10, 2) NOT NULL CHECK (total >= 0),
    placed_at   TIMESTAMPTZ    NOT NULL
);
CREATE INDEX orders_customer ON orders (customer_id, placed_at DESC);

CREATE TABLE order_lines (
    order_id   UUID           NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    line_no    INT            NOT NULL,
    isbn       VARCHAR(20)    NOT NULL,
    title      VARCHAR(300)   NOT NULL,
    quantity   INT            NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(10, 2) NOT NULL,
    PRIMARY KEY (order_id, line_no)
);

-- ADR-3: written in the order's transaction, published to Kafka by a relay (C.4)
CREATE TABLE outbox (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    topic        VARCHAR(100) NOT NULL,
    event_key    VARCHAR(100) NOT NULL,
    payload      TEXT         NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL,
    published_at TIMESTAMPTZ
);
CREATE INDEX outbox_unpublished ON outbox (id) WHERE published_at IS NULL;
