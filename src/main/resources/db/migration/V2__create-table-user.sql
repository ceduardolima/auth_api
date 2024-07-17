CREATE TABLE tb_user (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    account_id BIGINT NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_account
        FOREIGN KEY(account_id)
            REFERENCES account(id)
                ON DELETE CASCADE
                ON UPDATE CASCADE
);
