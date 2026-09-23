insert into author (name) values
    ('Joshua Bloch'), ('Raoul-Gabriel Urma'), ('Craig Walls'), ('Martin Fowler'), ('Mark Heckler');

insert into category (name) values ('java'), ('spring'), ('best-practices');

insert into book (isbn, title, author_id, price, stock, created_at, updated_at, version) values
    ('9780134685991', 'Effective Java',              1,  89.90,  5, now(), now(), 0),
    ('9780321336781', 'Java Puzzlers',               1,  55.00,  1, now(), now(), 0),
    ('9781617293566', 'Modern Java in Action',       2, 120.00, 10, now(), now(), 0),
    ('9781617297571', 'Spring in Action',            3,  95.00,  4, now(), now(), 0),
    ('9780134757599', 'Refactoring',                 4,  85.00,  2, now(), now(), 0),
    ('9781492076988', 'Spring Boot: Up and Running', 5, 110.00,  3, now(), now(), 0);

insert into book_category (book_id, category_id) values
    (1, 1), (1, 3), (2, 1), (3, 1), (4, 2), (5, 3), (6, 2), (6, 1);
