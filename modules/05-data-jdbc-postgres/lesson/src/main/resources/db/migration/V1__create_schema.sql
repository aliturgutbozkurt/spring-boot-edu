-- Lesson 3.2 — the schema is code: versioned, reviewed and applied by Flyway at startup.
-- tag::schema[]
create table author (
    id   bigserial primary key,
    name text      not null
);

create table book (
    id        bigserial     primary key,
    isbn      varchar(13)   not null unique,
    title     text          not null,
    author_id bigint        references author (id),
    price     numeric(10,2) not null check (price >= 0),
    stock     integer       not null default 0 check (stock >= 0)   -- the database guards the rule too
);
-- end::schema[]

-- Lesson 3.4 — the order aggregate: a root table and a child table
create table purchase_order (
    id             bigserial   primary key,
    customer_email text        not null,
    created_at     timestamptz not null
);

create table order_line (
    purchase_order bigint        not null references purchase_order (id) on delete cascade,
    isbn           varchar(13)   not null,
    quantity       integer       not null check (quantity > 0),
    unit_price     numeric(10,2) not null
);

-- Lessons 3.5–3.6
create table audit_log (
    id         bigserial   primary key,
    message    text        not null,
    created_at timestamptz not null default now()
);

create table notification (
    id         bigserial   primary key,
    message    text        not null,
    created_at timestamptz not null default now()
);
