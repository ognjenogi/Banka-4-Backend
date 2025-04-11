alter table otc_requests
    drop column made_by;

alter table otc_requests
    drop column made_for;

alter table otc_requests
    drop column modified_by;

alter table otc_requests
    add column made_by_routing_number bigint not null;

alter table otc_requests
    add column made_by_user_id varchar(255) not null;

alter table otc_requests
    add column made_for_routing_number bigint not null;

alter table otc_requests
    add column made_for_user_id varchar(255) not null;

alter table otc_requests
    add column modified_by_routing_number bigint not null;

alter table otc_requests
    add column modified_by_user_id varchar(255) not null;
