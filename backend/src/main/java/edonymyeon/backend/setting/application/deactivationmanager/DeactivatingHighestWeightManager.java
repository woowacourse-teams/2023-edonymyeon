package edonymyeon.backend.setting.application.deactivationmanager;

import edonymyeon.backend.setting.domain.Setting;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DeactivatingHighestWeightManager implements DeactivationManager {

    @Override
    public void manage(final List<Setting> settings, final Setting target) {
        for (Setting setting : settings) {
            if (setting.isSameCategoryWith(target) && !setting.hasHighestWeight() && setting.isActive()) {
                return;
            }
        }

        settings.stream()
                .filter(set -> set.isSameCategoryWith(target) && set.hasHighestWeight())
                .forEach(Setting::deactivate);
    }
}
