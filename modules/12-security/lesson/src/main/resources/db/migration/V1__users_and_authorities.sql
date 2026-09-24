-- tag::schema[]
-- The default schema of JdbcUserDetailsManager (see org/springframework/security/core/userdetails/jdbc/users.ddl)
CREATE TABLE users (
    username VARCHAR(50)  NOT NULL PRIMARY KEY,
    password VARCHAR(500) NOT NULL,              -- "{bcrypt}$2a$10$…": the prefix names the algorithm
    enabled  BOOLEAN      NOT NULL
);

CREATE TABLE authorities (
    username  VARCHAR(50) NOT NULL REFERENCES users (username),
    authority VARCHAR(50) NOT NULL               -- "ROLE_ADMIN" → hasRole('ADMIN')
);

CREATE UNIQUE INDEX ix_auth_username ON authorities (username, authority);
-- end::schema[]
