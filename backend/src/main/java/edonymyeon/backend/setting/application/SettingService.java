package edonymyeon.backend.setting.application;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.setting.domain.Setting;
import edonymyeon.backend.setting.repository.SettingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SettingService {

    private final SettingRepository settingRepository;

    public void initializeSettings(Member member) {
        final List<Setting> settings = Setting.makeInitializedSettings(member);
        settingRepository.saveAll(settings);
    }
}
