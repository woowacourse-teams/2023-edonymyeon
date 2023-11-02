package edonymyeon.backend.image.postimage.repository;

import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PostImageInfoCustomRepositoryImpl implements PostImageInfoCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchSave(final List<PostImageInfo> imageInfos, final Long postId) {
        final LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.batchUpdate("INSERT INTO post_image_info (created_at, post_id, store_name) values (?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        ps.setObject(1, now);
                        ps.setLong(2, postId);
                        ps.setString(3, imageInfos.get(i).getStoreName());
                    }

                    @Override
                    public int getBatchSize() {
                        return imageInfos.size();
                    }
                });
    }
}
