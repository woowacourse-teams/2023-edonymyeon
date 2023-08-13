package edonymyeon.backend.post.application;

import edonymyeon.backend.cache.util.CacheKeyStrategy;

public class TestCacheKeyStrategy implements CacheKeyStrategy {

    private static final String POST_IDS_CACHE_KEY = "TEST-HOT-POST:SIZE=%s_PAGE=%s";

    private static final String IS_LAST_CACHE_KEY = "TEST-HOT-POST-LAST:SIZE=%s_PAGE=%s";

    @Override
    public String getPostIdsCacheKey(final Integer size, final Integer page){
        return String.format(POST_IDS_CACHE_KEY, size, page);
    }

    @Override
    public String getLastCacheKey(final Integer size, final Integer page){
        return String.format(IS_LAST_CACHE_KEY, size, page);
    }
}
