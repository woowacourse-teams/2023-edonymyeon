package edonymyeon.backend.membber.setting.application.activationmanager;

import edonymyeon.backend.membber.setting.domain.Setting;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 설정 하나가 활성화된다면
 * 동일한 카테고리의 동일한 가중치를 가진 설정들은 모두 비활성화됩니다.
 */
@Component
@Transactional
public class DeactivatingSameWeightsManager extends ActivationManager {

    @Override
    public void manageSettings(final List<Setting> settings, final Setting target) {
        for (Setting setting : settings) {
            if (!setting.equals(target) && setting.isSameCategoryWith(target) && setting.hasSameWeight(target)) {
                setting.deactivate();
            }
        }
    }
}
