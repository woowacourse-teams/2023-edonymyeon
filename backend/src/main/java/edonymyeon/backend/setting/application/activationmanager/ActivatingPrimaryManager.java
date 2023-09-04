package edonymyeon.backend.setting.application.activationmanager;

import edonymyeon.backend.setting.domain.Setting;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ActivatingPrimaryManager implements ActivationManager {

    @Override
    public void manage(final List<Setting> settings, final Setting target) {
        for (Setting setting : settings) {
            if (setting.isPrimary()) {
                setting.activate();
            }
        }
    }
}
