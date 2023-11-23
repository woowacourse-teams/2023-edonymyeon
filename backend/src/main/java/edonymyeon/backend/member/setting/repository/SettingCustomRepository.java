package edonymyeon.backend.member.setting.repository;

import edonymyeon.backend.member.setting.domain.Setting;
import java.util.List;

public interface SettingCustomRepository {

    void batchSave(List<Setting> settings);
}
