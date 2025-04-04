create table assets
(
    id     uuid not null,
    name   varchar(255),
    ticker varchar(255),
    primary key (id)
);

insert into assets (id, name, ticker)
select id, name, name
from forex_pairs;
insert into assets (id, name, ticker)
select id, name, name
from futures;
insert into assets (id, name, ticker)
select id, name, name
from stocks;

create table securities
(
    id uuid not null,
    primary key (id)
);

insert into securities (id)
select id
from forex_pairs;
insert into securities (id)
select id
from futures;
insert into securities (id)
select id
from stocks;

alter table if exists forex_pairs
    add constraint fk_forex_pairs_securities
        foreign key (id)
            references securities;
alter table if exists forex_pairs
    drop column name;

alter table if exists futures
    add constraint fk_futures_securities
        foreign key (id)
            references securities;
alter table if exists futures
    drop column name;

alter table if exists listings
    add constraint fk_listings_securities
        foreign key (security_id)
            references securities;
alter table if exists listings
    drop column name;
alter table if exists listings
    drop column ticker;

alter table if exists options
    add constraint fk_options_assets
        foreign key (id)
            references assets;

alter table if exists orders
    add constraint fk_orders_assets
        foreign key (asset_id)
            references assets;

alter table if exists security
    add constraint fk_security_assets
        foreign key (id)
            references assets;

alter table if exists stocks
    add constraint fk_stocks_securities
        foreign key (id)
            references securities;

alter table if exists stocks
    drop column name;

alter table if exists listing_daily_price_info
    drop column name;
alter table if exists listing_daily_price_info
    drop column ticker;
alter table if exists listing_daily_price_info
    add column security_id uuid;
alter table if exists listing_daily_price_info
    add constraint fk_listing_daily_price_info_securities
        foreign key (security_id)
            references securities;
