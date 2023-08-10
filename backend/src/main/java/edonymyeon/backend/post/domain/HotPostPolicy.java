package edonymyeon.backend.post.domain;

import edonymyeon.backend.post.application.HotFindingCondition;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class HotPostPolicy {

    private static final int FINDING_PERIOD = 7;

    public static final int VIEW_COUNT_WEIGHT = 1;

    public static final int THUMBS_COUNT_WEIGHT = 3;

    private static final String POST_IDS_CACHE_KEY = "HOT_POST_SIZE:%s_PAGE:%s";

    private static final String IS_LAST_CACHE_KEY = "HOT_POST_SIZE:%s_PAGE:%s_LAST";

    public static LocalDateTime getFindPeriod(){
        return LocalDateTime.now()
                .minus(FINDING_PERIOD, ChronoUnit.DAYS);
    }

    public static String getPostIdsCacheKey(HotFindingCondition hotFindingCondition){
        return String.format(HotPostPolicy.POST_IDS_CACHE_KEY, hotFindingCondition.getSize(), hotFindingCondition.getPage());
    }

    public static String getLastCacheKey(HotFindingCondition hotFindingCondition){
        return String.format(HotPostPolicy.IS_LAST_CACHE_KEY, hotFindingCondition.getSize(), hotFindingCondition.getPage());
    }
}
