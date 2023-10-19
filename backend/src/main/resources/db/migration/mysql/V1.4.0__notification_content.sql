create table notification_content
(
    id    varchar(255) not null check (id in ('THUMBS_NOTIFICATION_TITLE', 'THUMBS_PER_10_NOTIFICATION_TITLE',
                                              'COMMENT_NOTIFICATION_TITLE', 'UNCONFIRMED_POST_REMINDER_TITLE')),
    body  varchar(255),
    title varchar(255),
    primary key (id)
);

alter table notification add body varchar(255);
