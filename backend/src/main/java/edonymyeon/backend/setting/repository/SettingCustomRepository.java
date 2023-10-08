package edonymyeon.backend.setting.repository;

import edonymyeon.backend.setting.domain.Setting;
import java.util.List;

public interface SettingCustomRepository {

    void batchSave(List<Setting> settings);
}
