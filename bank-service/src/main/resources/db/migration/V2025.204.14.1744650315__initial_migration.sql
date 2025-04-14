create
domain currency as varchar(255)
check (value in ('RSD', 'EUR', 'USD', 'CHF', 'JPY', 'AUD', 'CAD', 'GBP'));

create table accounts
(
    account_maintenance numeric(38, 2),
    active              boolean      not null,
    available_balance   numeric(38, 2),
    balance             numeric(38, 2),
    created_date        date,
    daily_limit         numeric(38, 2),
    expiration_date     date,
    monthly_limit       numeric(38, 2),
    client_id           uuid,
    company_id          uuid,
    employee_id         uuid,
    id                  uuid         not null,
    account_number      varchar(255) not null unique,
    account_type        varchar(255) check (account_type in
                                            ('STANDARD', 'SAVINGS', 'RETIREMENT', 'YOUTH', 'STUDENT', 'UNEMPLOYED',
                                             'DOO', 'AD', 'FOUNDATION')),
    currency_code currency not null,
    primary key (id)
);

create table activity_codes
(
    id     uuid         not null,
    branch varchar(255) not null,
    code   varchar(255) not null unique,
    sector varchar(255) not null,
    primary key (id)
);

create table bank_margins
(
    margin numeric(38, 2),
    id     uuid         not null,
    type   varchar(255) not null unique check (type in ('CASH', 'MORTGAGE', 'AUTO_LOAN', 'REFINANCING', 'STUDENT_LOAN')),
    primary key (id)
);

create table cards
(
    card_limit    numeric(38, 2),
    created_at    date,
    date_of_birth date,
    expires_at    date,
    account_id    uuid,
    id            uuid         not null,
    user_id       uuid,
    address       varchar(255),
    card_name     varchar(255) not null check (card_name in ('VISA', 'MASTER_CARD', 'DINA_CARD', 'AMERICAN_EXPRESS')),
    card_number   varchar(255) not null unique,
    card_status   varchar(255) check (card_status in ('ACTIVATED', 'DEACTIVATED', 'BLOCKED')),
    card_type     varchar(255) not null check (card_type in ('DEBIT', 'CREDIT')),
    cvv           varchar(255) not null,
    email         varchar(255),
    first_name    varchar(255),
    gender        varchar(255) check (gender in ('MALE', 'FEMALE')),
    last_name     varchar(255),
    phone_number  varchar(255),
    primary key (id)
);

create table client_contacts
(
    deleted        boolean      not null,
    client_id      uuid         not null,
    id             uuid         not null,
    account_number varchar(255) not null,
    nickname       varchar(255) not null,
    primary key (id)
);

create table clients
(
    id uuid not null,
    primary key (id)
);

create table companies
(
    activity_code_id  uuid,
    id                uuid         not null,
    majority_owner_id uuid,
    address           varchar(255) not null,
    crn               varchar(255) not null unique,
    name              varchar(255) not null unique,
    tin               varchar(255) not null unique,
    primary key (id)
);

create table employees
(
    active     boolean      not null,
    id         uuid         not null,
    department varchar(255) not null,
    position   varchar(255) not null,
    username   varchar(255) not null unique,
    primary key (id)
);

create table interest_rates
(
    date_active_from date           not null,
    date_active_to   date           not null,
    fixed_rate       numeric(38, 2) not null,
    max_amount       numeric(38, 2) not null,
    min_amount       numeric(38, 2) not null,
    id               uuid           not null,
    primary key (id)
);

create table loan_installments
(
    actual_due_date      date,
    expected_due_date    date,
    installment_amount   numeric(38, 2),
    interest_rate_amount numeric(38, 2),
    id                   uuid not null,
    loan_id              uuid,
    payment_status       varchar(255) check (payment_status in ('PAID', 'UNPAID', 'DELAYED')),
    primary key (id)
);

create table loan_requests
(
    amount            numeric(38, 2),
    employment_period integer,
    monthly_income    numeric(38, 2),
    repayment_period  integer,
    account_id        uuid,
    id                uuid not null,
    loan_id           uuid unique,
    contact_phone     varchar(255),
    currency_code currency,
    employment_status varchar(255),
    interest_type     varchar(255) check (interest_type in ('FIXED', 'VARIABLE')),
    purpose_of_loan   varchar(255),
    type              varchar(255) check (type in ('CASH', 'MORTGAGE', 'AUTO_LOAN', 'REFINANCING', 'STUDENT_LOAN')),
    primary key (id)
);

create table loans
(
    agreement_date        date,
    amount                numeric(38, 2),
    base_interest_rate    numeric(38, 2),
    due_date              date,
    monthly_installment   numeric(38, 2),
    next_installment_date date,
    remaining_debt        numeric(38, 2),
    repayment_period      integer,
    loan_number           bigint not null unique,
    account_id            uuid,
    id                    uuid   not null,
    interest_rate_id      uuid   not null,
    interest_type         varchar(255) check (interest_type in ('FIXED', 'VARIABLE')),
    status                varchar(255) check (status in ('APPROVED', 'REJECTED', 'PAID_OFF', 'DELAYED', 'PROCESSING')),
    type                  varchar(255) check (type in ('CASH', 'MORTGAGE', 'AUTO_LOAN', 'REFINANCING', 'STUDENT_LOAN')),
    primary key (id)
);

create table tokens
(
    valid boolean not null,
    id    uuid    not null,
    token varchar(255) unique,
    primary key (id)
);

create table transactions
(
    fee_amount         numeric(38, 2),
    from_amount        numeric(38, 2) not null,
    is_transfer        boolean        not null,
    payment_code       varchar(3),
    to_amount          numeric(38, 2) not null,
    payment_date_time  timestamp(6)   not null,
    from_account_id    uuid           not null,
    id                 uuid           not null,
    to_account_id      uuid           not null,
    reference_number   varchar(50),
    payment_purpose    varchar(500)   not null,
    fee_currency currency not null,
    from_currency currency not null,
    recipient          varchar(255)   not null,
    status             varchar(255)   not null check (status in ('REALIZED', 'REJECTED', 'IN_PROGRESS')),
    to_currency currency not null,
    transaction_number varchar(255)   not null unique,
    primary key (id)
);

create table user_to_totp_secrets
(
    is_active   boolean      not null,
    client_id   uuid unique,
    employee_id uuid unique,
    id          uuid         not null,
    secret      varchar(255) not null unique,
    primary key (id)
);

create table users
(
    date_of_birth   date         not null,
    enabled         boolean      not null,
    permission_bits bigint       not null,
    id              uuid         not null,
    address         varchar(255) not null,
    email           varchar(255) not null unique,
    first_name      varchar(255) not null,
    gender          varchar(255) not null check (gender in ('MALE', 'FEMALE')),
    last_name       varchar(255) not null,
    password        varchar(255),
    phone           varchar(255) not null,
    primary key (id)
);

create table verification_tokens
(
    used            boolean not null,
    expiration_date timestamp(6),
    id              bigint generated by default as identity,
    code            varchar(255),
    email           varchar(255),
    primary key (id)
);

alter table if exists accounts
    add constraint fk_accounts_clients
        foreign key (client_id)
            references clients;

alter table if exists accounts
    add constraint fk_accounts_companies
        foreign key (company_id)
            references companies;

alter table if exists accounts
    add constraint fk_accounts_employees
        foreign key (employee_id)
            references employees;

alter table if exists cards
    add constraint fk_cards_accounts
        foreign key (account_id)
            references accounts;

alter table if exists client_contacts
    add constraint fk_client_contacts_clients
        foreign key (client_id)
            references clients;

alter table if exists clients
    add constraint fk_clients_users
        foreign key (id)
            references users;

alter table if exists companies
    add constraint fk_companies_activity_codes
        foreign key (activity_code_id)
            references activity_codes;

alter table if exists companies
    add constraint fk_companies_clients
        foreign key (majority_owner_id)
            references clients;

alter table if exists employees
    add constraint fk_employees_users
        foreign key (id)
            references users;

alter table if exists loan_installments
    add constraint fk_loan_installments_loans
        foreign key (loan_id)
            references loans;

alter table if exists loan_requests
    add constraint fk_loan_requests_accounts
        foreign key (account_id)
            references accounts;

alter table if exists loan_requests
    add constraint fk_loan_requests_loans
        foreign key (loan_id)
            references loans;

alter table if exists loans
    add constraint fk_loans_accounts
        foreign key (account_id)
            references accounts;

alter table if exists loans
    add constraint fk_loans_interest_rates
        foreign key (interest_rate_id)
            references interest_rates;

alter table if exists transactions
    add constraint fk_transactions_from_accounts
        foreign key (from_account_id)
            references accounts;


alter table if exists transactions
    add constraint fk_transactions_to_accounts
        foreign key (to_account_id)
            references accounts;

alter table if exists user_to_totp_secrets
    add constraint fk_user_to_totp_secrets_clients
        foreign key (client_id)
            references clients;

alter table if exists user_to_totp_secrets
    add constraint fk_user_to_totp_secrets_employees
        foreign key (employee_id)
            references employees;


-- former stock service


create table actuary_informations
(
    limit_amount      numeric(38, 2) not null,
    need_approval     boolean        not null,
    used_limit_amount numeric(38, 2) not null,
    user_id           uuid           not null,
    limit_currency currency not null,
    used_limit_currency currency not null,
    primary key (user_id)
);

create table asset_ownership
(
    private_amount  integer not null,
    public_amount   integer not null,
    reserved_amount integer not null,
    id_asset_id     uuid    not null,
    id_user         uuid    not null,
    primary key (id_asset_id, id_user)
);

create table assets
(
    id     uuid not null,
    name   varchar(255),
    ticker varchar(255),
    primary key (id)
);

create table exchanges
(
    created_at       date         not null default now(),
    close_time       timestamp(6) with time zone not null,
    open_time        timestamp(6) with time zone not null,
    id               uuid         not null,
    currency currency not null,
    exchange_acronym varchar(255) not null,
    exchange_name    varchar(255) not null,
    exchangemiccode  varchar(255) not null,
    polity           varchar(255) not null,
    time_zone        varchar(255) not null,
    primary key (id)
);

create table forex_pairs
(
    exchange_rate numeric(38, 2) not null,
    id            uuid           not null,
    base_currency currency not null,
    liquidity     varchar(255)   not null check (liquidity in ('HIGH', 'MEDIUM', 'LOW')),
    quote_currency currency not null,
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
    security_id uuid           not null,
    primary key (id)
);

create table listings
(
    active        boolean        not null,
    ask           numeric(38, 2) not null,
    bid           numeric(38, 2) not null,
    contract_size integer        not null,
    last_refresh  timestamp(6) with time zone not null,
    exchange_id   uuid           not null,
    id            uuid           not null,
    security_id   uuid           not null,
    primary key (id)
);

create table options
(
    active              boolean        not null default true,
    implied_volatility  float(53)      not null,
    open_interest       integer        not null,
    premium_amount      numeric(38, 2) not null,
    strike_price_amount numeric(38, 2) not null,
    settlement_date     timestamp(6) with time zone not null,
    id                  uuid           not null,
    stock_id            uuid           not null,
    option_type         varchar(255)   not null check (option_type in ('CALL', 'PUT')),
    premium_currency currency not null,
    strike_price_currency currency not null,
    primary key (id)
);

create table orders
(
    after_hours           boolean        not null,
    all_or_nothing        boolean        not null,
    contract_size         integer        not null,
    is_done               boolean        not null,
    limit_value_amount    numeric(38, 2) not null,
    margin                boolean        not null,
    price_per_unit_amount numeric(38, 2) not null,
    quantity              integer        not null,
    remaining_portions    integer        not null,
    stop_value_amount     numeric(38, 2) not null,
    used                  boolean        not null,
    created_at            timestamp(6) with time zone not null default now(),
    last_modified         timestamp(6) with time zone not null,
    account_id            uuid           not null,
    approved_by           uuid,
    asset_id              uuid           not null,
    id                    uuid           not null,
    user_id               uuid           not null,
    direction             varchar(255)   not null check (direction in ('BUY', 'SELL')),
    limit_value_currency currency not null,
    order_type            varchar(255)   not null check (order_type in ('MARKET', 'LIMIT', 'STOP', 'STOP_LIMIT')),
    price_per_unit_currency currency not null,
    status                varchar(255)   not null check (status in ('PENDING', 'APPROVED', 'DECLINED')),
    stop_value_currency currency not null,
    primary key (id)
);

create table otc_requests
(
    amount                     integer        not null,
    premium_amount             numeric(38, 2) not null,
    price_per_stock_amount     numeric(38, 2) not null,
    settlement_date            date           not null,
    last_modified              timestamp(6) with time zone not null,
    made_by_routing_number     bigint         not null,
    made_for_routing_number    bigint         not null,
    modified_by_routing_number bigint         not null,
    id                         uuid           not null,
    option_id                  uuid,
    stock_id                   uuid           not null,
    made_by_user_id            varchar(255)   not null,
    made_for_user_id           varchar(255)   not null,
    modified_by_user_id        varchar(255)   not null,
    premium_currency currency not null,
    price_per_stock_currency currency not null,
    status                     varchar(255)   not null check (status in ('ACTIVE', 'REJECTED', 'FINISHED', 'USED', 'EXPIRED')),
    primary key (id)
);

create table securities
(
    id uuid not null,
    primary key (id)
);

create table stocks
(
    dividend_yield     numeric(38, 2) not null,
    created_at         timestamp(6) with time zone not null default now(),
    outstanding_shares bigint         not null,
    id                 uuid           not null,
    primary key (id)
);

alter table if exists asset_ownership
    add constraint fk_asset_ownership_assets
        foreign key (id_asset_id)
            references assets;

alter table if exists asset_ownership
    add constraint fk_asset_ownership_users
        foreign key (id_user)
            references users;

alter table if exists actuary_informations
    add constraint fk_actuary_informations_users
        foreign key (user_id)
            references users;

alter table if exists forex_pairs
    add constraint fk_forex_pairs_securities
        foreign key (id)
            references securities;

alter table if exists futures
    add constraint fk_futures_securities
        foreign key (id)
            references securities;

alter table if exists listing_daily_price_info
    add constraint fk_listing_daily_price_info_exchanges
        foreign key (exchange_id)
            references exchanges;

alter table if exists listing_daily_price_info
    add constraint fk_listing_daily_price_info_securities
        foreign key (security_id)
            references securities;

alter table if exists listings
    add constraint fk_listings_exchanges
        foreign key (exchange_id)
            references exchanges;

alter table if exists listings
    add constraint fk_listings_securities
        foreign key (security_id)
            references securities;

alter table if exists options
    add constraint fk_options_stocks
        foreign key (stock_id)
            references stocks;

alter table if exists options
    add constraint fk_options_assets
        foreign key (id)
            references assets;

alter table if exists orders
    add constraint fk_orders_assets
        foreign key (asset_id)
            references assets;

alter table if exists orders
    add constraint fk_orders_users
        foreign key (user_id)
            references users;

alter table if exists orders
    add constraint fk_orders_accounts
        foreign key (account_id)
            references accounts;

alter table if exists otc_requests
    add constraint fk_otc_requests_stocks
        foreign key (stock_id)
            references stocks;

alter table if exists securities
    add constraint fk_securities_assets
        foreign key (id)
            references assets;

alter table if exists stocks
    add constraint fk_stocks_securities
        foreign key (id)
            references securities;

-- https://media.discordapp.net/attachments/823931853688799282/1056542086855393320/caption.gif?ex=67fe4a7f&is=67fcf8ff&hm=bd7df5554f44d73103fa943a5e3271156cd3b417f77f9fe5c8b5cb731eec752a&
