package edonymyeon.backend.setting.application.deactivationmanager;

import edonymyeon.backend.setting.domain.Setting;
import java.util.List;

public interface DeactivationManager {
    void manage(final List<Setting> settings, final Setting target);
}
