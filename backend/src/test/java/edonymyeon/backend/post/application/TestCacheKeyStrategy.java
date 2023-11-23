package edonymyeon.backend.post.application;

import edonymyeon.backend.content.cache.util.CacheKeyStrategy;

public class TestCacheKeyStrategy implements CacheKeyStrategy {

    private static final String CACHE_NAME_FORMAT = "TEST:SIZE=%s_PAGE=%s";

    private static final int EXPIRED_SECONDS = 1;

    @Override
    public String geCacheKey(final Integer size, final Integer page){
        return String.format(CACHE_NAME_FORMAT, size, page);
    }

    @Override
    public int getExpiredSeconds() {
        return EXPIRED_SECONDS;
    }
}
