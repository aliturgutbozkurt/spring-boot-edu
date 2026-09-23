-- Given: the order tables of the exercises.
create table purchase_order (
    id             bigserial   primary key,
    customer_email text        not null,
    status         varchar(10) not null check (status in ('NEW', 'PAID', 'SHIPPED')),
    created_at     timestamptz not null
);

create table order_line (
    id         bigserial     primary key,
    order_id   bigint        not null references purchase_order (id) on delete cascade,
    isbn       varchar(13)   not null,
    quantity   integer       not null check (quantity > 0),
    unit_price numeric(10,2) not null
);
