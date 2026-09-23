-- Exercise 1 — reviews of books
create table review (
    id         bigserial   primary key,
    book_id    bigint      not null references book (id) on delete cascade,
    stars      smallint    not null check (stars between 1 and 5),
    comment    text,
    created_at timestamptz not null default now()
);

create index review_book_id_idx on review (book_id);
