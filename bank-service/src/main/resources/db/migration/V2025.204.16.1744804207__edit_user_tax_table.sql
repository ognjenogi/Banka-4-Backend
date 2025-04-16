alter table user_tax_debts
    drop constraint user_tax_debts_user_id_fkey;

alter table user_tax_debts
    drop column user_id;

alter table user_tax_debts
    add column account_id uuid unique not null references accounts (id);

alter table user_tax_debts
    add column yearly_debt_amount numeric(38, 2) not null default 0;
