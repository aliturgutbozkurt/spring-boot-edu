CREATE TABLE book (
    isbn  VARCHAR(13)    PRIMARY KEY,
    title VARCHAR(200)   NOT NULL,
    price NUMERIC(10, 2) NOT NULL
);

INSERT INTO book (isbn, title, price) VALUES
    ('9780134685991', 'Effective Java', 89.90),
    ('9781617297571', 'Spring in Action', 95.00),
    ('9781449373320', 'Designing Data-Intensive Applications', 110.00);
