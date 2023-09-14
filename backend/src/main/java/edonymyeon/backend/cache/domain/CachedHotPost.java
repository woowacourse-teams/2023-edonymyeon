package edonymyeon.backend.cache.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class CachedHotPost {

    private List<Long> postIds;

    private boolean isLast;

    private LocalDateTime refreshTime;

    public boolean shouldRefresh(final int expiredSeconds) {
        LocalDateTime now = LocalDateTime.now();
        Duration between = Duration.between(refreshTime, now);
        return between.toSeconds() >= expiredSeconds;
    }

    public void refreshData(final List<Long> hotPostIds, final boolean isLast) {
        initializePostIds();
        this.postIds.addAll(hotPostIds);
        this.isLast = isLast;
        refreshTime = LocalDateTime.now();
    }

    private void initializePostIds() {
        this.postIds = new ArrayList<>();
    }

    public List<Long> getPostIds() {
        if(Objects.isNull(this.postIds)){
            return Collections.emptyList();
        }
        return this.postIds;
    }
}
