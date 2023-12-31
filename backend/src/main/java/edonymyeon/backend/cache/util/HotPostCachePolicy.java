package edonymyeon.backend.cache.util;

import edonymyeon.backend.post.application.HotFindingCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HotPostCachePolicy {

    private final CacheKeyStrategy cacheKeyStrategy;

    public String getKey(HotFindingCondition hotFindingCondition){
        return cacheKeyStrategy.geCacheKey(hotFindingCondition.getSize(), hotFindingCondition.getPage());
    }

    public int getExpiredSeconds() {
        return cacheKeyStrategy.getExpiredSeconds();
    }
}
