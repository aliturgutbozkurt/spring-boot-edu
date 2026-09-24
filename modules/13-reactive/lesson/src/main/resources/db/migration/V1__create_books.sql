CREATE TABLE book (
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn   VARCHAR(13)    NOT NULL UNIQUE,
    title  VARCHAR(200)   NOT NULL,
    price  NUMERIC(10, 2) NOT NULL CHECK (price >= 0)
);

INSERT INTO book (isbn, title, price) VALUES
    ('9780134685991', 'Effective Java', 89.90),
    ('9781617297571', 'Spring in Action', 95.00),
    ('9780321336781', 'Java Puzzlers', 55.00);
