ALTER TABLE bank_accounts
    ADD COLUMN idempotency_key VARCHAR(200);

CREATE UNIQUE INDEX uq_bank_accounts_idempotency_key
    ON bank_accounts (idempotency_key)
    WHERE idempotency_key IS NOT NULL;
