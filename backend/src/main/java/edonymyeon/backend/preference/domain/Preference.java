package edonymyeon.backend.preference.domain;

import edonymyeon.backend.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class Preference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @Enumerated(EnumType.STRING)
    private PreferenceKey preferenceKey;

    @ManyToMany
    @JoinTable(name = "preferences_dependent_preferences",
            joinColumns = @JoinColumn(name = "preference_id"),
            inverseJoinColumns = @JoinColumn(name = "dependent_preference_id")
    )
    private List<Preference> dependentPreferences;

    private EnableStatus enabled;

    public Preference(final Member member, final PreferenceKey preferenceKey,
                      final List<Preference> dependentPreferences, final EnableStatus enabled) {
        this.member = member;
        this.preferenceKey = preferenceKey;
        this.dependentPreferences = dependentPreferences;
        this.enabled = enabled;
    }

    public static List<Preference> makeInitializedPreferences(Member member) {
        ArrayList<Preference> preferences = new ArrayList<>();
        for (PreferenceKey preferenceKey : PreferenceKey.values()) {
            preferences.add(new Preference(member, preferenceKey, new ArrayList<>(), EnableStatus.DISABLED));
        }

        for (Preference preference : preferences) {
            final List<Preference> dependentPreferences = preferences.stream()
                    .filter(pref -> pref.isDependentBy(preference))
                    .toList();
            preference.dependentPreferences.addAll(dependentPreferences);
        }

        return preferences;
    }

    private boolean isDependentBy(final Preference preference) {
        return this.preferenceKey.isDependentBy(preference.preferenceKey);
    }

    public void toggleEnable() {
        this.enabled = this.enabled.toggle();
        handleDependentPreferences();
    }

    private void handleDependentPreferences() {
        if (this.enabled.isEnabled()) {
            for (Preference dependentPreference : dependentPreferences) {
                dependentPreference.releaseForceShut();
            }
        }

        if (!this.enabled.isEnabled()) {
            for (Preference dependentPreference : dependentPreferences) {
                dependentPreference.forceShut();
            }
        }
    }

    private void releaseForceShut() {
        this.enabled = this.enabled.releaseForceDisable();
    }

    private void forceShut() {
        this.enabled = this.enabled.forceDisable();
    }

    public boolean isEnabled() {
        return this.enabled.isEnabled();
    }
}
