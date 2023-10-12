package edonymyeon.backend.setting.repository;

import edonymyeon.backend.setting.domain.Setting;
import edonymyeon.backend.setting.domain.SettingType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long>, SettingCustomRepository {

    List<Setting> findByMemberId(Long memberId);

    Setting findByMemberIdAndSettingType(Long memberId, SettingType settingType);
}
