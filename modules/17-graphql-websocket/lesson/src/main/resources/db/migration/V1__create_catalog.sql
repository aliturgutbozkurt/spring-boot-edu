CREATE TABLE author (
    id   BIGINT PRIMARY KEY,
    name VARCHAR(200) NOT NULL
);

CREATE TABLE book (
    isbn      VARCHAR(13)    PRIMARY KEY,
    title     VARCHAR(200)   NOT NULL,
    price     NUMERIC(10, 2) NOT NULL,
    author_id BIGINT         NOT NULL REFERENCES author (id)
);

CREATE TABLE review (
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn  VARCHAR(13) NOT NULL REFERENCES book (isbn),
    stars INT         NOT NULL CHECK (stars BETWEEN 1 AND 5),
    text  TEXT        NOT NULL
);

INSERT INTO author (id, name) VALUES (1, 'Joshua Bloch'), (2, 'Craig Walls'), (3, 'Martin Kleppmann');

INSERT INTO book (isbn, title, price, author_id) VALUES
    ('9780134685991', 'Effective Java', 89.90, 1),
    ('9780321336781', 'Java Puzzlers', 55.00, 1),
    ('9781617297571', 'Spring in Action', 95.00, 2),
    ('9781449373320', 'Designing Data-Intensive Applications', 110.00, 3);

INSERT INTO review (isbn, stars, text) VALUES
    ('9780134685991', 5, 'A classic.'),
    ('9780134685991', 4, 'Dense but worth it.'),
    ('9781617297571', 4, 'Very practical.');
