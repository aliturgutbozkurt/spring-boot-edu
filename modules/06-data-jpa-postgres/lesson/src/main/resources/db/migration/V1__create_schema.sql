-- Flyway owns the schema; Hibernate only validates it (spring.jpa.hibernate.ddl-auto=validate).
create table author (
    id   bigserial primary key,
    name text      not null unique
);

create table category (
    id   bigserial primary key,
    name text      not null unique
);

create table book (
    id         bigserial     primary key,
    isbn       varchar(13)   not null unique,
    title      text          not null,
    author_id  bigint        not null references author (id),
    price      numeric(10,2) not null check (price >= 0),
    stock      integer       not null check (stock >= 0),
    created_at timestamptz   not null,
    updated_at timestamptz   not null,
    version    bigint        not null                 -- optimistic locking (Lesson 3.6)
);

create table book_category (                          -- the join table of a many-to-many relationship
    book_id     bigint not null references book (id) on delete cascade,
    category_id bigint not null references category (id),
    primary key (book_id, category_id)
);
