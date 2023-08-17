package edonymyeon.backend.setting.application;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.setting.domain.Setting;
import edonymyeon.backend.setting.domain.SettingType;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.support.IntegrationTest;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

@RequiredArgsConstructor
@IntegrationTest
class SettingServiceTest extends IntegrationFixture {

    private final SettingService settingService;

    private static void 설정_간_의존성을_검사한다(final Setting setting) {
        final SettingType settingType = getPreferenceKey(setting);
        final List<Setting> dependentSettings = getDependentPreferences(setting);
        for (Setting dependentSetting : dependentSettings) {
            final SettingType dependentSettingType = getPreferenceKey(dependentSetting);
            assertThat(dependentSettingType.isDependentBy(settingType)).isTrue();
        }
    }

    private static SettingType getPreferenceKey(final Setting setting) {
        final Field preferenceKeyField = ReflectionUtils.findField(Setting.class, "settingType");
        ReflectionUtils.makeAccessible(preferenceKeyField);
        return (SettingType) ReflectionUtils.getField(preferenceKeyField, setting);
    }

    private static List<Setting> getDependentPreferences(final Setting setting) {
        final Field dependentPreferencesField = ReflectionUtils.findField(Setting.class, "dependentSettings");
        ReflectionUtils.makeAccessible(dependentPreferencesField);
        return (List<Setting>) ReflectionUtils.getField(dependentPreferencesField, setting);
    }

    private static List<Setting> findPreferencesByMember(final EntityManager entityManager, final Member member) {
        return entityManager
                .createQuery(
                        "select p from Setting p left join fetch p.dependentSettings where p.member.id = :memberId",
                        Setting.class)
                .setParameter("memberId", member.getId())
                .getResultList();
    }

    @Test
    void 유저_가입_후_설정을_초기화할_수_있다(@Autowired EntityManager entityManager) {
        final Member member = 사용자를_하나_만든다();
        settingService.initializeSettings(member);

        final List<Setting> settings = findPreferencesByMember(entityManager, member);

        assertThat(settings)
                .as("존재하는 설정 항목의 개수만큼 초기화한다.").hasSize(SettingType.values().length);

        for (Setting setting : settings) {
            설정_간_의존성을_검사한다(setting);
        }
    }

    @Test
    void 초기화된_설정은_전부_비활성화_되어있다(@Autowired EntityManager entityManager) {
        final Member member = 사용자를_하나_만든다();
        settingService.initializeSettings(member);
        final List<Setting> settings = findPreferencesByMember(entityManager, member);
        for (Setting setting : settings) {
            assertThat(setting.isEnabled()).isFalse();
        }
    }
}
