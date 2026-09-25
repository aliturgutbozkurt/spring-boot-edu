CREATE TABLE stock (
    isbn      VARCHAR(13)  PRIMARY KEY,
    title     VARCHAR(200) NOT NULL,
    available INT          NOT NULL
);

INSERT INTO stock (isbn, title, available) VALUES
    ('9780134685991', 'Effective Java', 12),
    ('9781617297571', 'Spring in Action', 0),
    ('9781449373320', 'Designing Data-Intensive Applications', 4),
    ('9780596007126', 'Head First Design Patterns', 7);
