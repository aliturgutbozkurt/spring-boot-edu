CREATE TABLE imported_book (
    isbn        VARCHAR(13)    PRIMARY KEY,
    title       VARCHAR(200)   NOT NULL,
    price       NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    imported_at TIMESTAMPTZ    NOT NULL DEFAULT now()
);
