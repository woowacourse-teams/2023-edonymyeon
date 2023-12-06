package edonymyeon.backend.member.setting.application.activationmanager;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.setting.domain.Setting;
import java.util.List;

public abstract class ActivationManager {

    public void manage(List<Setting> settings, Setting target) {
        if (!target.isActive()) {
            throw new BusinessLogicException(ExceptionInformation.INVALID_SETTING_MANAGER_ASSIGNED);
        }
        manageSettings(settings, target);
    }

    protected abstract void manageSettings(List<Setting> settings, Setting target);
}
