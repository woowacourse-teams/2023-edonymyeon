package edonymyeon.backend.setting.application.deactivationmanager;

import edonymyeon.backend.setting.domain.Setting;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 같은 카테고리의
 * 어떤 설정이 비활성화 되는 경우
 * 해당 설정보다 낮은 가중치의 설정들은
 * 자동으로 비활성화 됩니다.
 */
@Component
@Transactional
public class DeactivatingLowerWeightManager implements DeactivationManager {

    @Override
    public void manage(final List<Setting> settings, final Setting target) {
        for (Setting setting : settings) {
            if (setting.isSameCategoryWith(target) && setting.hasLowerWeightThan(target)) {
                setting.deactivate();
            }
        }
    }
}
