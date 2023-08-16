package edonymyeon.backend.cache.util;

public interface CacheKeyStrategy {

    String getPostIdsCacheKey(final Integer size, final Integer page);

    String getLastCacheKey(final Integer size, final Integer page);

    int getExpiredSeconds();
}
