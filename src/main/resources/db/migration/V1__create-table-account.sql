CREATE TABLE account (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(20) NOT NULL
);

CREATE INDEX account_email_index ON account(email);