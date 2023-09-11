alter table device drop foreign key device_ibfk_1;
alter table device add constraint fk_device_member_id foreign key (member_id) references member (id);

alter table notification drop foreign key notification_ibfk_1;
alter table notification add constraint fk_notification_member_id foreign key (member_id) references member (id);

alter table notification drop check notification_chk_1;
alter table notification add constraint chk_notification_screen_type check (screen_type in ('POST', 'MYPOST'));

alter table setting drop check setting_chk_1;
alter table setting add constraint chk_setting_setting_type check (setting_type in ('NOTIFICATION_PER_THUMBS','NOTIFICATION_PER_10_THUMBS','NOTIFICATION_PER_COMMENT','NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING','NOTIFICATION'));

alter table report drop check report_chk_1;
alter table report add constraint chk_report_report_type check (report_type in ('POST','COMMENT'));
