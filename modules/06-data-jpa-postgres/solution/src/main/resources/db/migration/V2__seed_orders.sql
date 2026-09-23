-- Given: four orders of two customers.
insert into purchase_order (customer_email, status, created_at) values
    ('ayse@example.com',   'NEW',     '2026-09-01T10:00:00Z'),
    ('ayse@example.com',   'PAID',    '2026-09-10T10:00:00Z'),
    ('ayse@example.com',   'SHIPPED', '2026-09-15T10:00:00Z'),
    ('mehmet@example.com', 'PAID',    '2026-09-12T10:00:00Z');

insert into order_line (order_id, isbn, quantity, unit_price) values
    (1, '9780134685991', 1, 89.90),
    (1, '9780321336781', 2, 55.00),
    (2, '9780134757599', 1, 85.00),
    (3, '9780134685991', 1, 89.90),
    (4, '9781617297571', 2, 95.00),
    (4, '9780134685991', 1, 89.90);
