package edonymyeon.backend.cache.util;

import org.springframework.stereotype.Component;

@Component
public class ProductCacheKeyStrategy implements CacheKeyStrategy {

    private static final String POST_IDS_CACHE_KEY = "HOT-POST:SIZE=%s_PAGE=%s";

    private static final String IS_LAST_CACHE_KEY = "HOT-POST-LAST:SIZE=%s_PAGE=%s";

    private static final int EXPIRED_MINUTE = 30;

    private static final int SECONDS_PER_MINUTE = 60;

    @Override
    public String getPostIdsCacheKey(final Integer size, final Integer page) {
        return String.format(POST_IDS_CACHE_KEY, size, page);
    }

    @Override
    public String getLastCacheKey(final Integer size, final Integer page) {
        return String.format(IS_LAST_CACHE_KEY, size, page);
    }

    @Override
    public int getExpiredSeconds() {
        return EXPIRED_MINUTE * SECONDS_PER_MINUTE;
    }
}
