alter table actuary_informations
    alter column limit_amount drop not null;

alter table actuary_informations
    alter column used_limit_amount set default 0;
