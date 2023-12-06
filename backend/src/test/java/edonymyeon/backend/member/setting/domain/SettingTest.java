package edonymyeon.backend.member.setting.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SettingTest {
    @Test
    void 설정_활성화여부를_토글링_할_수_있다() {
        final Setting setting = new Setting(SettingType.NOTIFICATION_PER_THUMBS, null);
        assertThat(setting.isActive())
                .as("기본값은 비활성 상태")
                .isFalse();

        setting.activate();
        assertThat(setting.isActive()).isTrue();

        setting.deactivate();
        assertThat(setting.isActive()).isFalse();
    }
}
