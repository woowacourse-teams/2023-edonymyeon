package edonymyeon.backend.cache.application.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@RedisHash("HOT_POSTS")
public class CachedHotPost {

    @Id
    private final String id;

    private List<Long> postIds;

    private boolean isLast;

    private LocalDateTime refreshTime;

    public boolean shouldRefresh(final int expiredSeconds) {
        LocalDateTime now = LocalDateTime.now();
        Duration between = Duration.between(refreshTime, now);
        return between.toSeconds() >= expiredSeconds;
    }

    public void refreshData(final List<Long> hotPostIds, final boolean isLast) {
        this.postIds.clear();
        this.postIds.addAll(hotPostIds);
        this.isLast = isLast;
        refreshTime = LocalDateTime.now();
    }
}
