create
domain currency as varchar(255) check (value in('RSD', 'EUR', 'USD', 'CHF', 'JPY', 'AUD', 'CAD', 'GBP'));

alter table exchanges
    drop constraint exchanges_currency_check;

alter table orders
    drop constraint orders_limit_value_currency_check;

alter table orders
    drop constraint orders_price_per_unit_currency_check;

alter table orders
    drop constraint orders_stop_value_currency_check;

alter table options
    drop constraint options_strike_price_currency_check;

alter table forex_pairs
    drop constraint forex_pairs_base_currency_check;

alter table forex_pairs
    drop constraint forex_pairs_quote_currency_check;

alter table exchanges
    alter column currency type currency using currency::currency;

alter table orders
    alter column limit_value_currency type currency using limit_value_currency::currency;

alter table orders
    alter column price_per_unit_currency type currency using price_per_unit_currency::currency;

alter table orders
    alter column stop_value_currency type currency using stop_value_currency::currency;

alter table options
    alter column strike_price_currency type currency using strike_price_currency::currency;

alter table forex_pairs
    alter column base_currency type currency using base_currency::currency;

alter table forex_pairs
    alter column quote_currency type currency using quote_currency::currency;
