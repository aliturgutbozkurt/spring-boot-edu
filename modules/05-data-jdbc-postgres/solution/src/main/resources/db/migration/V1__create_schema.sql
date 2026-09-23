-- Given: the bookstore schema of the exercises.
create table author (
    id   bigserial primary key,
    name text      not null
);

create table book (
    id        bigserial     primary key,
    isbn      varchar(13)   not null unique,
    title     text          not null,
    author_id bigint        references author (id),
    price     numeric(10,2) not null check (price >= 0)
);

-- Exercise 3: one row per import attempt
create table import_log (
    id         bigserial   primary key,
    isbn       varchar(13) not null,
    status     text        not null check (status in ('SUCCESS', 'FAILED')),
    detail     text,
    created_at timestamptz not null default now()
);
