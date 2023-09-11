alter table device drop foreign key device_ibfk_1;
alter table device add constraint fk_device_member_id foreign key (member_id) references member (id);

alter table notification drop foreign key notification_ibfk_1;
alter table notification add constraint fk_notification_member_id foreign key (member_id) references member (id);
