package edonymyeon.backend.preference.application;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.preference.domain.Preference;
import edonymyeon.backend.preference.domain.PreferenceKey;
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
class PreferenceServiceTest extends IntegrationFixture {

    private final PreferenceService preferenceService;

    private static void 설정_간_의존성을_검사한다(final Preference preference) {
        final PreferenceKey preferenceKey = getPreferenceKey(preference);
        final List<Preference> dependentPreferences = getDependentPreferences(preference);
        for (Preference dependentPreference : dependentPreferences) {
            final PreferenceKey dependentPreferenceKey = getPreferenceKey(dependentPreference);
            assertThat(dependentPreferenceKey.isDependentBy(preferenceKey)).isTrue();
        }
    }

    private static PreferenceKey getPreferenceKey(final Preference preference) {
        final Field preferenceKeyField = ReflectionUtils.findField(Preference.class, "preferenceKey");
        ReflectionUtils.makeAccessible(preferenceKeyField);
        return (PreferenceKey) ReflectionUtils.getField(preferenceKeyField, preference);
    }

    private static List<Preference> getDependentPreferences(final Preference preference) {
        final Field dependentPreferencesField = ReflectionUtils.findField(Preference.class, "dependentPreferences");
        ReflectionUtils.makeAccessible(dependentPreferencesField);
        return (List<Preference>) ReflectionUtils.getField(dependentPreferencesField, preference);
    }

    private static List<Preference> findPreferencesByMember(final EntityManager entityManager, final Member member) {
        return entityManager
                .createQuery(
                        "select p from Preference p left join fetch p.dependentPreferences where p.member.id = :memberId",
                        Preference.class)
                .setParameter("memberId", member.getId())
                .getResultList();
    }

    @Test
    void 유저_가입_후_설정을_초기화할_수_있다(@Autowired EntityManager entityManager) {
        final Member member = 사용자를_하나_만든다();
        preferenceService.initializeMemberPreference(member);

        final List<Preference> preferences = findPreferencesByMember(entityManager, member);

        assertThat(preferences)
                .as("존재하는 설정 항목의 개수만큼 초기화한다.").hasSize(PreferenceKey.values().length);

        for (Preference preference : preferences) {
            설정_간_의존성을_검사한다(preference);
        }
    }

    @Test
    void 초기화된_설정은_전부_비활성화_되어있다(@Autowired EntityManager entityManager) {
        final Member member = 사용자를_하나_만든다();
        preferenceService.initializeMemberPreference(member);
        final List<Preference> preferences = findPreferencesByMember(entityManager, member);
        for (Preference preference : preferences) {
            assertThat(preference.isEnabled()).isFalse();
        }
    }
}
