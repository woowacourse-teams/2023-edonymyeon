package edonymyeon.backend.post.domain;

import edonymyeon.backend.post.application.HotFindingCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Component
public class HotPostPolicy {

    private static final int FINDING_PERIOD = 7;

    private static final int VIEW_COUNT_WEIGHT = 1;

    private static final int THUMBS_COUNT_WEIGHT = 3;


    private final CacheKeyStrategy cacheKeyStrategy;

    public LocalDateTime getFindPeriod(){
        return LocalDateTime.now()
                .minus(FINDING_PERIOD, ChronoUnit.DAYS);
    }

    public int getViewCountWeight(){
        return VIEW_COUNT_WEIGHT;
    }

    public int getThumbsCountWeight(){
        return THUMBS_COUNT_WEIGHT;
    }

    public String getPostIdsCacheKey(HotFindingCondition hotFindingCondition){
        return cacheKeyStrategy.getPostIdsCacheKey(hotFindingCondition.getSize(), hotFindingCondition.getPage());
    }

    public String getLastCacheKey(HotFindingCondition hotFindingCondition){
        return cacheKeyStrategy.getLastCacheKey(hotFindingCondition.getSize(), hotFindingCondition.getPage());
    }
}
