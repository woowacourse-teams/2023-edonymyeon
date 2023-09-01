package edonymyeon.backend.setting.domain;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.support.TestMemberBuilder;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class SettingTest {
    private final TestMemberBuilder testMemberBuilder = new TestMemberBuilder(null);

    @Test
    void 설정_항목을_체크하여_OnOff할_수_있다() {
        final Member member = testMemberBuilder.builder()
                .id(1L)
                .buildWithoutSaving();
        final Setting setting
                = new Setting(member, SettingType.NOTIFICATION_PER_THUMB, Collections.emptyList(),
                EnableStatus.ENABLED);

        setting.toggleEnable();

        assertThat(setting.isEnabled()).isFalse();
    }

    @Test
    void 상위_항목을_disable하는_경우_하위_항목도_함께_disable된다() {
        final Member member = testMemberBuilder.builder()
                .id(1L)
                .buildWithoutSaving();

        final Setting 하위항목1 = new Setting(member, SettingType.NOTIFICATION_PER_THUMB, Collections.emptyList(),
                EnableStatus.ENABLED);
        final Setting 하위항목2 = new Setting(member, SettingType.NOTIFICATION_PER_10, Collections.emptyList(),
                EnableStatus.ENABLED);
        final Setting 상위항목 = new Setting(member, SettingType.NOTIFICATION_THUMB, List.of(하위항목1, 하위항목2),
                EnableStatus.ENABLED);
        assertThat(하위항목1.isEnabled()).isTrue();
        assertThat(하위항목2.isEnabled()).isTrue();

        상위항목.toggleEnable();

        assertThat(하위항목1.isEnabled()).isFalse();
        assertThat(하위항목2.isEnabled()).isFalse();
    }

    @Test
    void 상위_항목에_의해_disable된_항목이_상위_항목에_의해_다시_activated된_경우_이전의_설정을_다시_복구한다() {
        final Member member = testMemberBuilder.builder()
                .id(1L)
                .buildWithoutSaving();

        final Setting 하위항목1 = new Setting(member, SettingType.NOTIFICATION_PER_THUMB, Collections.emptyList(),
                EnableStatus.ENABLED);
        final Setting 하위항목2 = new Setting(member, SettingType.NOTIFICATION_PER_10, Collections.emptyList(),
                EnableStatus.DISABLED);
        final Setting 상위항목 = new Setting(member, SettingType.NOTIFICATION_THUMB, List.of(하위항목1, 하위항목2),
                EnableStatus.ENABLED);
        assertThat(하위항목1.isEnabled()).isTrue();
        assertThat(하위항목2.isEnabled()).isFalse();

        상위항목.toggleEnable();

        assertThat(하위항목1.isEnabled()).isFalse();
        assertThat(하위항목2.isEnabled()).isFalse();

        상위항목.toggleEnable();

        assertThat(하위항목1.isEnabled()).isTrue();
        assertThat(하위항목2.isEnabled()).isFalse();
    }
}
