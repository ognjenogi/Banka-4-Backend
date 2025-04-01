-- Drop the artificial UUID from currencies, let them be identified via their
-- currency code only.

-- Make currency.code mandatory.
ALTER TABLE currencies ALTER COLUMN code SET NOT NULL;

ALTER TABLE accounts
      DROP CONSTRAINT fk_accounts_on_currency;
ALTER TABLE transactions
      DROP CONSTRAINT fk_fee_currency;
ALTER TABLE transactions
      DROP CONSTRAINT fk_from_currency;
ALTER TABLE loan_requests
      DROP CONSTRAINT fk_loan_requests_on_currency;
ALTER TABLE transactions
      DROP CONSTRAINT fk_to_currency;

-- Change primary key of currency table.
ALTER TABLE currencies DROP CONSTRAINT pk_currencies;
ALTER TABLE currencies ADD CONSTRAINT pk_currencies PRIMARY KEY (code);

-- Due to the FKs above, the following queries should always produce the right
-- currency code.

-- Rework 'accounts' table.
ALTER TABLE accounts
      ADD COLUMN currency_code VARCHAR(255);
UPDATE accounts AS acc
       SET currency_code = (SELECT code FROM currencies AS c
                                        WHERE c.id = acc.currency_id);
ALTER TABLE accounts
      DROP COLUMN currency_id;
ALTER TABLE accounts
      ADD CONSTRAINT fk_accounts_on_currency FOREIGN KEY (currency_code)
          REFERENCES currencies (code);


-- Rework 'transactions' table.
ALTER TABLE transactions
      ADD COLUMN fee_currency_code VARCHAR(255);
UPDATE transactions AS tx
       SET fee_currency_code = (SELECT code FROM currencies AS c
                                            WHERE c.id = tx.fee_currency_id);
ALTER TABLE transactions
      DROP COLUMN fee_currency_id;
ALTER TABLE transactions
      ADD CONSTRAINT fk_fee_currency FOREIGN KEY (fee_currency_code)
          REFERENCES currencies (code);

ALTER TABLE transactions
      ADD COLUMN from_currency_code VARCHAR(255);
UPDATE transactions AS tx
       SET from_currency_code = (SELECT code FROM currencies AS c
                                             WHERE c.id = tx.from_currency_id);
ALTER TABLE transactions
      DROP COLUMN from_currency_id;
ALTER TABLE transactions
      ADD CONSTRAINT fk_from_currency FOREIGN KEY (from_currency_code)
          REFERENCES currencies (code);

ALTER TABLE transactions
      ADD COLUMN to_currency_code VARCHAR(255);
UPDATE transactions AS tx
       SET to_currency_code = (SELECT code FROM currencies AS c
                                           WHERE c.id = tx.to_currency_id);
ALTER TABLE transactions
      DROP COLUMN to_currency_id;
ALTER TABLE transactions
      ADD CONSTRAINT fk_to_currency FOREIGN KEY (to_currency_code)
          REFERENCES currencies (code);


-- Rework 'loan_requests' table.
ALTER TABLE loan_requests
      ADD COLUMN currency_code VARCHAR(255);
UPDATE loan_requests AS lrq
       SET currency_code = (SELECT code FROM currencies AS c
                                        WHERE c.id = lrq.currency_id);
ALTER TABLE loan_requests
      DROP COLUMN currency_id;
ALTER TABLE loan_requests
      ADD CONSTRAINT fk_loan_requests_on_currency FOREIGN KEY (currency_code)
          REFERENCES currencies (code);

-- Drop ID column, finally.
ALTER TABLE currencies DROP COLUMN id;
