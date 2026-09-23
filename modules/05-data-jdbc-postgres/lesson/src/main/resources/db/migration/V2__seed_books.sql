-- Lesson 3.2 — a second migration: sample data. Never edit an applied migration; add a new one.
insert into author (name) values
    ('Joshua Bloch'), ('Raoul-Gabriel Urma'), ('Craig Walls'), ('Martin Fowler');

insert into book (isbn, title, author_id, price, stock) values
    ('9780134685991', 'Effective Java',        1,  89.90,  5),
    ('9780321336781', 'Java Puzzlers',         1,  55.00,  1),
    ('9781617293566', 'Modern Java in Action', 2, 120.00, 10),
    ('9781617297571', 'Spring in Action',      3,  95.00,  4),
    ('9780134757599', 'Refactoring',           4,  85.00,  2);
