CREATE TABLE stock (
    isbn      VARCHAR(13) PRIMARY KEY,
    available INT         NOT NULL CHECK (available >= 0)
);

CREATE TABLE orders (
    id          VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    isbn        VARCHAR(13) NOT NULL REFERENCES stock (isbn),
    quantity    INT         NOT NULL CHECK (quantity > 0),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- tag::outbox-table[]
CREATE TABLE outbox (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,   -- also the sending order
    aggregate_id VARCHAR(36)  NOT NULL,                             -- becomes the Kafka key
    event_type   VARCHAR(100) NOT NULL,
    payload      TEXT         NOT NULL,                             -- the event as JSON
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    sent_at      TIMESTAMPTZ                                        -- NULL = not yet sent
);

CREATE INDEX outbox_unsent ON outbox (id) WHERE sent_at IS NULL;    -- the relay only reads unsent rows
-- end::outbox-table[]

INSERT INTO stock (isbn, available) VALUES
    ('9780134685991', 1000),
    ('9781617297571', 1000),
    ('9780321336781', 1000);
