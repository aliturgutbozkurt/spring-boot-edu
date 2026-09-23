CREATE TABLE book (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn        VARCHAR(13)   NOT NULL UNIQUE,
    title       VARCHAR(200)  NOT NULL,
    author      VARCHAR(200)  NOT NULL,
    description TEXT          NOT NULL,
    category    VARCHAR(50)   NOT NULL,
    price       NUMERIC(10, 2) NOT NULL CHECK (price >= 0)
);
