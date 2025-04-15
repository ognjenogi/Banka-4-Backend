create table user_tax_debts
(
    id          uuid           not null,
    user_id     uuid           not null unique references users (id),
    debt_amount numeric(38, 2) not null default 0,
    primary key (id)
);
