DROP INDEX account_email_index;

CREATE UNIQUE INDEX account_email_index ON account(email);
