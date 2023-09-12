DROP TABLE settings_dependent_settings;
DROP TABLE setting;

create table setting (
                         is_active boolean not null,
                         id bigint auto_increment,
                         member_id bigint,
                         setting_type varchar(255) check (setting_type in ('NOTIFICATION_PER_THUMBS','NOTIFICATION_PER_10_THUMBS','NOTIFICATION_PER_COMMENT','NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING','NOTIFICATION')),
                         primary key (id)
);

alter table setting add constraint fk_setting_member_id foreign key (member_id) references member (id);
