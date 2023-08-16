package edonymyeon.backend.cache.util;

import org.springframework.stereotype.Component;

@Component
public class ProductCacheKeyStrategy implements CacheKeyStrategy {

    private static final String CACHE_NAME_FORMAT = "PROD:SIZE=%s_PAGE=%s";

    private static final int EXPIRED_MINUTE = 30;

    private static final int SECONDS_PER_MINUTE = 60;

    @Override
    public String geCacheKey(final Integer size, final Integer page) {
        return String.format(CACHE_NAME_FORMAT, size, page);
    }

    @Override
    public int getExpiredSeconds() {
        return EXPIRED_MINUTE * SECONDS_PER_MINUTE;
    }
}
