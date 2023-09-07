alter table report drop foreign key report_ibfk_1;
alter table report change column post_id reference_id bigint;
alter table report add report_type varchar(255) check (report_type in ('POST','COMMENT'));
