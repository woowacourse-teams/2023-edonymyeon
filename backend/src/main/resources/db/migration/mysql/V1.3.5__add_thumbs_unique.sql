alter table thumbs
add constraint uk_thumbs_post_id_member_id unique (post_id, member_id);
