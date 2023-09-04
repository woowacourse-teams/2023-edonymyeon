package edonymyeon.backend.setting.application.deactivationmanager;

import edonymyeon.backend.setting.domain.Setting;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DeactivatingPrimaryManager implements DeactivationManager {

    @Override
    public void manage(final List<Setting> settings, final Setting target) {
        if (target.isPrimary()) {
            for (Setting setting : settings) {
                setting.deactivate();
            }
        }
    }
}
