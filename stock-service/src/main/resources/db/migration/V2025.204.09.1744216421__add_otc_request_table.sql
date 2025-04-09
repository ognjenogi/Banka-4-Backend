create table otc_requests
(
    id                     uuid           not null,
    stock_id               uuid           not null,
    price_per_stock_amount numeric(38, 2) not null,
    price_per_stock_currency currency not null,
    premium_amount         numeric(38, 2) not null,
    premium_currency currency not null,
    amount                 integer        not null,
    made_by                uuid           not null,
    made_for               uuid           not null,
    modified_by            uuid           not null,
    settlement_date        date           not null,
    last_modified          timestamp with time zone not null,
    status                 varchar(255)   not null check (status in ('ACTIVE', 'REJECTED', 'FINISHED')),
    option_id              uuid
);

alter table if exists otc_requests
    add constraint fk_otc_requests_stocks
        foreign key (stock_id)
            references stocks;

alter table if exists otc_requests
    add constraint check_request_finished
        check ( (option_id is null) <> (status = 'FINISHED') )
