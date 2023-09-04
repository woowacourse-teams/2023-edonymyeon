package edonymyeon.backend.setting.application.deactivationmanager;

import edonymyeon.backend.setting.domain.Setting;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 어떤 카테고리에서
 * 가중치가 10 미만인 설정둘이 모두 비활성화 되었다면
 * 가중치가 가장 높은(10) 설정은 자동으로 비활성화됩니다.
 */
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
