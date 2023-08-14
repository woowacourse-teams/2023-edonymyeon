create table if not exists profile_image_info (
    id bigint not null auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    store_name varchar(255),
    primary key (id)
);

create table if not exists member (
    id bigint not null auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    age integer,
    email varchar(255) not null,
    nickname varchar(255) not null,
    password varchar(255) not null,
    profile_image_info_id bigint,
    primary key (id),
    unique key (email),
    unique key (nickname),
    unique key (profile_image_info_id),
    foreign key (profile_image_info_id) references profile_image_info (id)
);

create table if not exists post (
    id bigint not null auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    content longtext not null,
    price bigint not null,
    title varchar(255) not null,
    view_count integer default 0 not null,
    member_id bigint not null,
    primary key (id),
    foreign key (member_id) references member (id)
);

create table if not exists post_image_info (
    id bigint not null auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    store_name varchar(255),
    post_id bigint,
    primary key (id),
    foreign key (post_id) references post (id)
);

create table if not exists thumbs (
    id bigint not null auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    thumbs_type enum ('DOWN','UP'),
    member_id bigint,
    post_id bigint,
    primary key (id),
    foreign key (member_id) references member (id),
    foreign key (post_id) references post (id)
);

create table if not exists consumption (
    id bigint not null auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    consumption_date date not null,
    consumption_type enum ('PURCHASE','SAVING') not null,
    price bigint not null,
    post_id bigint,
    primary key (id),
    unique key (post_id),
    foreign key (post_id) references post (id) on delete cascade
);

create table if not exists report (
    id bigint not null auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    abusing_type tinyint,
    additional_comment varchar(255),
    post_id bigint,
    reporter_id bigint,
    primary key (id),
    foreign key (reporter_id) references member (id),
    foreign key (post_id) references post (id)
);
