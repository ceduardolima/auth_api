CREATE TABLE tb_email_confirmation_token (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    token VARCHAR(255) NOT NULL,
    createdAt TIMESTAMP NOT NULL,
    expiresAt TIMESTAMP NOT NULL,
    confirmedAt TIMESTAMP NOT NULL,
    account_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_account
        FOREIGN KEY(account_id)
            REFERENCES account(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE
);
