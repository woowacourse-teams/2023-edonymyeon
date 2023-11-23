package edonymyeon.backend.membber.setting.repository;

import edonymyeon.backend.membber.setting.domain.Setting;
import java.util.List;

public interface SettingCustomRepository {

    void batchSave(List<Setting> settings);
}
