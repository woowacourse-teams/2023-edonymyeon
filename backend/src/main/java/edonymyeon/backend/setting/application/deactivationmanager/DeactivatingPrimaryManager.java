package edonymyeon.backend.setting.application.deactivationmanager;

import edonymyeon.backend.setting.domain.Setting;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 어떤 카테고리에서
 * 가중치가 가장 높은(10) 설정이 비활성화된다면
 * 해당 카테고리의 모든 설정이 함께 비활성화됩니다.
 */
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
