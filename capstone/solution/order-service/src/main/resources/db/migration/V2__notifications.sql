-- one confirmation per order: the primary key makes a redelivered OrderPlaced harmless
CREATE TABLE notifications (
    order_id    UUID PRIMARY KEY,
    customer_id VARCHAR(100) NOT NULL,
    message     VARCHAR(500) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL
);
