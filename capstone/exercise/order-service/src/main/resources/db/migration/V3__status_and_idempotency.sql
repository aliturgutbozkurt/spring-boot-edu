-- Capstone exercises 1 and 2: an order can be cancelled, and a client can send an idempotency key
ALTER TABLE orders ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PLACED';
ALTER TABLE orders ADD COLUMN idempotency_key VARCHAR(100);
-- one order per key and customer; orders without a key are not affected (partial index)
CREATE UNIQUE INDEX orders_idempotency ON orders (customer_id, idempotency_key) WHERE idempotency_key IS NOT NULL;
