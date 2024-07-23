DROP TABLE tb_email_confirmation_token;
CREATE TABLE tb_email_confirmation_token (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    account_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_account
        FOREIGN KEY(account_id)
            REFERENCES account(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE
);
