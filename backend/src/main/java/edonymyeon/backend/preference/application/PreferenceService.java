package edonymyeon.backend.preference.application;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.preference.domain.Preference;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;

    public void initializeMemberPreference(Member member) {
        final List<Preference> preferences = Preference.makeInitializedPreferences(member);
        preferenceRepository.saveAll(preferences);
    }
}
