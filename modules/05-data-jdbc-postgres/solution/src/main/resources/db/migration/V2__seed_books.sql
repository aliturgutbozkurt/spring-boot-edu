-- Given: sample data.
insert into author (name) values ('Joshua Bloch'), ('Martin Fowler');

insert into book (isbn, title, author_id, price) values
    ('9780134685991', 'Effective Java', 1, 89.90),
    ('9780134757599', 'Refactoring',    2, 85.00);
