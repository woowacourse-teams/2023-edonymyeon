package edonymyeon.backend.post.domain;

import org.springframework.stereotype.Component;

@Component
public class ProductCacheKeyStrategy implements CacheKeyStrategy{

    private static final String POST_IDS_CACHE_KEY = "HOT_POST_SIZE:%s_PAGE:%s";

    private static final String IS_LAST_CACHE_KEY = "HOT_POST_SIZE:%s_PAGE:%s_LAST";

    @Override
    public String getPostIdsCacheKey(final Integer size, final Integer page){
        return String.format(POST_IDS_CACHE_KEY, size, page);
    }

    @Override
    public String getLastCacheKey(final Integer size, final Integer page){
        return String.format(IS_LAST_CACHE_KEY, size, page);
    }
}
