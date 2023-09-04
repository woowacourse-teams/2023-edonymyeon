package edonymyeon.backend.setting.application.activationmanager;

import edonymyeon.backend.setting.domain.Setting;
import java.util.List;

public interface ActivationManager {
    void manage(List<Setting> settings, Setting target);
}
