package edonymyeon.backend.cache.util;

import edonymyeon.backend.post.application.HotFindingCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HotPostCachePolicy {

    private final CacheKeyStrategy cacheKeyStrategy;

    public String getPostIdsCacheKey(HotFindingCondition hotFindingCondition){
        return cacheKeyStrategy.getPostIdsCacheKey(hotFindingCondition.getSize(), hotFindingCondition.getPage());
    }

    public String getLastCacheKey(HotFindingCondition hotFindingCondition){
        return cacheKeyStrategy.getLastCacheKey(hotFindingCondition.getSize(), hotFindingCondition.getPage());
    }
}
