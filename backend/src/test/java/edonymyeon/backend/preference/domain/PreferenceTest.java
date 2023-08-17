package edonymyeon.backend.preference.domain;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.member.domain.Member;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class PreferenceTest {
    @Test
    void 설정_항목을_체크하여_OnOff할_수_있다() {
        final Member member = new Member(1L, "test@gmail.com", "password123", "nickName", null, null);
        final Preference preference
                = new Preference(member, PreferenceType.NOTIFICATION_PER_THUMB, Collections.emptyList(),
                EnableStatus.ENABLED);

        preference.toggleEnable();

        assertThat(preference.isEnabled()).isFalse();
    }

    @Test
    void 상위_항목을_disable하는_경우_하위_항목도_함께_disable된다() {
        final Member member = new Member(1L, "test@gmail.com", "password123", "nickName", null, null);

        final Preference 하위항목1 = new Preference(member, PreferenceType.NOTIFICATION_PER_THUMB, Collections.emptyList(),
                EnableStatus.ENABLED);
        final Preference 하위항목2 = new Preference(member, PreferenceType.NOTIFICATION_PER_10, Collections.emptyList(),
                EnableStatus.ENABLED);
        final Preference 상위항목 = new Preference(member, PreferenceType.NOTIFICATION_THUMB, List.of(하위항목1, 하위항목2),
                EnableStatus.ENABLED);
        assertThat(하위항목1.isEnabled()).isTrue();
        assertThat(하위항목2.isEnabled()).isTrue();

        상위항목.toggleEnable();

        assertThat(하위항목1.isEnabled()).isFalse();
        assertThat(하위항목2.isEnabled()).isFalse();
    }

    @Test
    void 상위_항목에_의해_disable된_항목이_상위_항목에_의해_다시_activated된_경우_이전의_설정을_다시_복구한다() {
        final Member member = new Member(1L, "test@gmail.com", "password123", "nickName", null, null);

        final Preference 하위항목1 = new Preference(member, PreferenceType.NOTIFICATION_PER_THUMB, Collections.emptyList(),
                EnableStatus.ENABLED);
        final Preference 하위항목2 = new Preference(member, PreferenceType.NOTIFICATION_PER_10, Collections.emptyList(),
                EnableStatus.DISABLED);
        final Preference 상위항목 = new Preference(member, PreferenceType.NOTIFICATION_THUMB, List.of(하위항목1, 하위항목2),
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
