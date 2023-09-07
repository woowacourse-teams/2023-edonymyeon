create table comment_image_info (
    id bigint not null auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    store_name varchar(255),
    deleted bit default false not null,
    primary key (id)
);

create table comment (
    id bigint not null auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    content varchar(255) not null,
    deleted bit default false not null,
    comment_image_info_id bigint,
    member_id bigint not null,
    post_id bigint not null,
    primary key (id)
);

alter table comment add constraint uk_comment_comment_image_info_id unique (comment_image_info_id);
alter table comment add constraint fk_comment_comment_image_info_id foreign key (comment_image_info_id) references comment_image_info (id);
alter table comment add constraint fk_comment_member_id foreign key (member_id) references member (id);
alter table comment add constraint fk_comment_post_id foreign key (post_id) references post (id);
