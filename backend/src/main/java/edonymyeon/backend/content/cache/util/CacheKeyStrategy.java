package edonymyeon.backend.content.cache.util;

public interface CacheKeyStrategy {

    String geCacheKey(final Integer size, final Integer page);

    int getExpiredSeconds();
}
