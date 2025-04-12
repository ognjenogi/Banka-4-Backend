alter table otc_requests
    drop constraint otc_requests_status_check;

alter table otc_requests
    add constraint otc_requests_status_check
        check (status in ('ACTIVE', 'REJECTED', 'FINISHED', 'USED', 'EXPIRED'));
