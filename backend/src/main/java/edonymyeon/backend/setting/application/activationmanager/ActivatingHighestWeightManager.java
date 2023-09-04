package edonymyeon.backend.setting.application.activationmanager;

import edonymyeon.backend.setting.domain.Setting;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 같은 카테고리의
 * 가중치가 10 미만인 설정들 중 하나라도 활성화가 된다면
 * 가장 높은 가중치(10)를 가진 설정은 자동으로 활성화됩니다.
 */
@Component
@Transactional
public class ActivatingHighestWeightManager implements ActivationManager {

    @Override
    public void manage(final List<Setting> settings, final Setting target) {
        for (Setting setting : settings) {
            if (setting.isSameCategoryWith(target) && setting.hasHighestWeight()) {
                setting.activate();
            }
        }
    }
}
