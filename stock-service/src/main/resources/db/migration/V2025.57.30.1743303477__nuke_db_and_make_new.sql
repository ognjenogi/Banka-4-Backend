drop table exchanges cascade;
drop table forex_pairs cascade;
drop table futures cascade;
drop table stocks cascade;
drop table listings cascade;
drop table listing_daily_price_info cascade;

create table actuary_informations
(
    limit_amount        numeric(38, 2),
    need_approval       boolean not null,
    used_limit_amount   numeric(38, 2),
    user_id             uuid    not null,
    limit_currency      varchar(255) check (limit_currency in ('RSD', 'EUR', 'USD', 'CHF', 'JPY', 'AUD', 'CAD')),
    used_limit_currency varchar(255) check (used_limit_currency in ('RSD', 'EUR', 'USD', 'CHF', 'JPY', 'AUD', 'CAD')),
    primary key (user_id)
);

create table exchanges
(
    created_at       date,
    close_time       timestamp(6) with time zone not null,
    open_time        timestamp(6) with time zone not null,
    id               uuid         not null,
    currency         varchar(255) not null check (currency in ('RSD', 'EUR', 'USD', 'CHF', 'JPY', 'AUD', 'CAD')),
    exchange_acronym varchar(255) not null,
    exchange_name    varchar(255) not null,
    exchangemiccode  varchar(255) not null,
    polity           varchar(255) not null,
    time_zone        varchar(255) not null,
    primary key (id)
);

create table forex_pairs
(
    exchange_rate  numeric(38, 2) not null,
    id             uuid           not null,
    base_currency  varchar(255)   not null check (base_currency in ('RSD', 'EUR', 'USD', 'CHF', 'JPY', 'AUD', 'CAD')),
    liquidity      varchar(255)   not null check (liquidity in ('HIGH', 'MEDIUM', 'LOW')),
    name           varchar(255),
    quote_currency varchar(255)   not null check (quote_currency in ('RSD', 'EUR', 'USD', 'CHF', 'JPY', 'AUD', 'CAD')),
    primary key (id)
);

create table futures
(
    contract_size   bigint       not null,
    settlement_date timestamp(6) with time zone not null,
    id              uuid         not null,
    contract_unit   varchar(255) not null check (contract_unit in
                                                 ('BUSHEL', 'POUND', 'BOARD_FEET', 'BARREL', 'MMBTU', 'GALLON',
                                                  'TROY_OUNCE', 'METRIC_TON', 'SHORT_TON', 'CUBIC_FEET', 'LITER',
                                                  'GRAM', 'KILOGRAM', 'OUNCE', 'CARAT', 'QUART', 'MILLILITER',
                                                  'HECTOLITER', 'MEGAWATT_HOUR', 'CWT', 'BOTTLE', 'DOZEN', 'YARD',
                                                  'FOOT', 'INCH', 'SQUARE_FEET', 'SQUARE_METER', 'CORD',
                                                  'BUSHEL_WEIGHT', 'KILOPOUND', 'DRAM')),
    name            varchar(255),
    primary key (id)
);

create table listing_daily_price_info
(
    ask_high    numeric(38, 2) not null,
    big_low     numeric(38, 2) not null,
    change      numeric(38, 2) not null,
    last_price  numeric(38, 2) not null,
    volume      integer        not null,
    date        timestamp(6) with time zone not null,
    exchange_id uuid           not null,
    id          uuid           not null,
    name        varchar(255)   not null,
    ticker      varchar(255)   not null,
    primary key (id)
);

create table listings
(
    active        boolean        not null,
    ask           numeric(38, 2) not null,
    bid           numeric(38, 2) not null,
    contract_size integer        not null,
    last_refresh  timestamp(6) with time zone not null,
    exchange_id   uuid,
    id            uuid           not null,
    security_id   uuid,
    name          varchar(255)   not null,
    ticker        varchar(255)   not null,
    primary key (id)
);

create table options
(
    implied_volatility    float(53)    not null,
    open_interest         integer      not null,
    strike_price_amount   numeric(38, 2),
    settlement_date       timestamp(6) with time zone not null,
    id                    uuid         not null,
    stock_id              uuid         not null,
    option_type           varchar(255) not null check (option_type in ('CALL', 'PUT')),
    strike_price_currency varchar(255) check (strike_price_currency in
                                              ('RSD', 'EUR', 'USD', 'CHF', 'JPY', 'AUD', 'CAD')),
    primary key (id)
);

create table orders
(
    after_hours             boolean      not null,
    all_or_nothing          boolean      not null,
    contract_size           integer      not null,
    is_done                 boolean      not null,
    limit_value_amount      numeric(38, 2),
    margin                  boolean      not null,
    price_per_unit_amount   numeric(38, 2),
    quantity                integer      not null,
    remaining_portions      integer      not null,
    stop_value_amount       numeric(38, 2),
    last_modified           timestamp(6) with time zone not null,
    account_id              uuid         not null,
    approved_by             uuid,
    asset_id                uuid,
    id                      uuid         not null,
    user_id                 uuid         not null,
    direction               varchar(255) not null check (direction in ('BUY', 'SELL')),
    limit_value_currency    varchar(255) check (limit_value_currency in
                                                ('RSD', 'EUR', 'USD', 'CHF', 'JPY', 'AUD', 'CAD')),
    order_type              varchar(255) not null check (order_type in ('MARKET', 'LIMIT', 'STOP', 'STOP_LIMIT')),
    price_per_unit_currency varchar(255) check (price_per_unit_currency in
                                                ('RSD', 'EUR', 'USD', 'CHF', 'JPY', 'AUD', 'CAD')),
    status                  varchar(255) not null check (status in ('PENDING', 'APPROVED', 'DECLINED')),
    stop_value_currency     varchar(255) check (stop_value_currency in ('RSD', 'EUR', 'USD', 'CHF', 'JPY', 'AUD', 'CAD')),
    primary key (id)
);

create table stocks
(
    dividend_yield     numeric(38, 2) not null,
    created_at         timestamp(6) with time zone,
    outstanding_shares bigint         not null,
    id                 uuid           not null,
    name               varchar(255),
    primary key (id)
);

alter table if exists listing_daily_price_info
    add constraint fk_listing_daily_price_info_exchanges
        foreign key (exchange_id)
            references exchanges;

alter table if exists listings
    add constraint fk_listings_exchanges
        foreign key (exchange_id)
            references exchanges;

alter table if exists options
    add constraint fk_options_stocks
        foreign key (stock_id)
            references stocks;
