-- Demo users for local development only. Passwords: ada-password, bob-password, admin-password
INSERT INTO users (username, password, enabled) VALUES
    ('ada',   '{bcrypt}$2a$10$EQsTvqBLiau9q9Qj8U/s7OMgTolLZ12rMvtng/8oXYLhmiNkEhF2a', true),
    ('bob',   '{bcrypt}$2a$10$93a3VLVQSQVjgAoppWTMueWkTfsgwmgRBFzA.ja4TkTxkUYOCYSGu', true),
    ('admin', '{bcrypt}$2a$10$XlR7Y6rjZSK1h2633GlqFOjr7itWSFea2lObZlccOzacKWyAJjub6', true);

INSERT INTO authorities (username, authority) VALUES
    ('ada',   'ROLE_CUSTOMER'),
    ('bob',   'ROLE_CUSTOMER'),
    ('admin', 'ROLE_ADMIN');
