CREATE TABLE book (
    isbn  VARCHAR(13)    PRIMARY KEY,
    title VARCHAR(200)   NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    year  INT            NOT NULL
);

INSERT INTO book (isbn, title, price, year) VALUES
    ('9780134685991', 'Effective Java', 89.90, 2018),
    ('9780321336781', 'Java Puzzlers', 55.00, 2005),
    ('9780321349606', 'Java Concurrency in Practice', 79.00, 2006),
    ('9781617297571', 'Spring in Action', 95.00, 2022),
    ('9781449373320', 'Designing Data-Intensive Applications', 110.00, 2017);
