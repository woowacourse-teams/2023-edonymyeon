package edonymyeon.backend.member.setting.application.deactivationmanager;

import edonymyeon.backend.member.setting.domain.Setting;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ALL 카테고라에서 가중치가 가장 높은(10) 설정이 비활성화된다면
 * 존재하는 모든 설정이 함께 비활성화됩니다.
 */
@Component
@Transactional
public class DeactivatingPrimaryManager extends DeactivationManager {

    @Override
    public void manageSettings(final List<Setting> settings, final Setting target) {
        if (target.isPrimary()) {
            for (Setting setting : settings) {
                setting.deactivate();
            }
        }
    }
}
