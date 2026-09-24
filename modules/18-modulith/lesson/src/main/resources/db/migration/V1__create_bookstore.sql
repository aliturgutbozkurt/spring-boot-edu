-- catalog module
CREATE TABLE book (
    isbn  VARCHAR(13)    PRIMARY KEY,
    title VARCHAR(200)   NOT NULL,
    price NUMERIC(10, 2) NOT NULL
);

-- inventory module
CREATE TABLE stock (
    isbn      VARCHAR(13) PRIMARY KEY,
    available INT         NOT NULL
);

-- order module
CREATE TABLE orders (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id VARCHAR(50)    NOT NULL,
    isbn        VARCHAR(13)    NOT NULL,
    quantity    INT            NOT NULL,
    total       NUMERIC(10, 2) NOT NULL
);

INSERT INTO book (isbn, title, price) VALUES
    ('9780134685991', 'Effective Java', 89.90),
    ('9780321336781', 'Java Puzzlers', 55.00),
    ('9781617297571', 'Spring in Action', 95.00),
    ('9781449373320', 'Designing Data-Intensive Applications', 110.00);

INSERT INTO stock (isbn, available) VALUES
    ('9780134685991', 1000), ('9780321336781', 1000), ('9781617297571', 1000), ('9781449373320', 1000);
