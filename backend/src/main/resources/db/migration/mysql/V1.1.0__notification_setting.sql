CREATE TABLE device
(
    is_active    BOOLEAN NOT NULL,
    created_at   DATETIME(6),
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id    BIGINT,
    modified_at  DATETIME(6),
    device_token VARCHAR(255)
);

CREATE TABLE notification
(
    is_read     BOOLEAN NOT NULL,
    created_at  DATETIME(6),
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT  NOT NULL,
    modified_at DATETIME(6),
    post_id     BIGINT,
    screen_type VARCHAR(255) CHECK (screen_type IN ('POST', 'MYPOST')),
    title       VARCHAR(255)
);

CREATE TABLE setting
(
    enabled      TINYINT CHECK (enabled BETWEEN 0 AND 3),
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id    BIGINT,
    setting_type VARCHAR(255) CHECK (setting_type IN
                                     ('NOTIFICATION_PER_THUMB', 'NOTIFICATION_PER_10', 'NOTIFICATION_THUMB',
                                      'NOTIFICATION'))
);

CREATE TABLE settings_dependent_settings
(
    dependent_setting_id BIGINT NOT NULL,
    setting_id           BIGINT NOT NULL,
    FOREIGN KEY (dependent_setting_id) REFERENCES setting (id),
    FOREIGN KEY (setting_id) REFERENCES setting (id)
);

ALTER TABLE device
    ADD FOREIGN KEY (member_id) REFERENCES member (id);
ALTER TABLE notification
    ADD FOREIGN KEY (member_id) REFERENCES member (id);
ALTER TABLE setting
    ADD FOREIGN KEY (member_id) REFERENCES member (id);
ALTER TABLE settings_dependent_settings
    ADD FOREIGN KEY (dependent_setting_id) REFERENCES setting (id);
ALTER TABLE settings_dependent_settings
    ADD FOREIGN KEY (setting_id) REFERENCES setting (id);
