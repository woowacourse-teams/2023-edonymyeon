package edonymyeon.backend.membber.setting.application.deactivationmanager;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.membber.setting.domain.Setting;
import java.util.List;

public abstract class DeactivationManager {

    public void manage(List<Setting> settings, Setting target) {
        if (target.isActive()) {
            throw new BusinessLogicException(ExceptionInformation.INVALID_SETTING_MANAGER_ASSIGNED);
        }
        manageSettings(settings, target);
    }

    protected abstract void manageSettings(final List<Setting> settings, final Setting target);
}
