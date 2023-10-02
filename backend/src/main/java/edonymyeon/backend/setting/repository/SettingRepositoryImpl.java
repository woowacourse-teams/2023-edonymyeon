package edonymyeon.backend.setting.repository;

import edonymyeon.backend.setting.domain.Setting;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class SettingRepositoryImpl implements SettingCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 다수의 Setting을 하나의 insert 쿼리로 저장해주는 메서드입니다.
     * @param settings 저장할 Settings들입니다
     */
    @Override
    public void batchSave(final List<Setting> settings) {
        jdbcTemplate.batchUpdate("INSERT INTO setting (is_active, member_id, setting_type) values (?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        ps.setBoolean(1, settings.get(i).isActive());
                        ps.setLong(2, settings.get(i).findMemberId());
                        ps.setString(3, settings.get(i).findSettingName());
                    }

                    @Override
                    public int getBatchSize() {
                        return settings.size();
                    }
                });
    }
}
