package edonymyeon.backend.cache.application;

import edonymyeon.backend.cache.application.dto.CachedPostResponse;
import edonymyeon.backend.cache.util.HotPostCachePolicy;
import edonymyeon.backend.post.application.HotFindingCondition;
import edonymyeon.backend.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CachePostService {

    private final CacheIsLastService cacheIsLastService;

    private final CachePostIdsService cachePostIdsService;

    private final HotPostCachePolicy hotPostPolicy;

    public boolean isPostsCached(final HotFindingCondition hotFindingCondition) {
        String postIdsKey = hotPostPolicy.getPostIdsCacheKey(hotFindingCondition);
        return cachePostIdsService.hasCache(postIdsKey);
    }

    public CachedPostResponse findCachedPosts(HotFindingCondition hotFindingCondition) {
        String postIdsKey = hotPostPolicy.getPostIdsCacheKey(hotFindingCondition);
        String isLastKey = hotPostPolicy.getLastCacheKey(hotFindingCondition);

        List<Long> postIds = cachePostIdsService.getPostIds(postIdsKey);
        boolean isLast = cacheIsLastService.getHasNext(isLastKey);
        return new CachedPostResponse(postIds, isLast);
    }

    public void cachePosts(final HotFindingCondition hotFindingCondition, final Slice<Post> hotPost) {
        String postIdsKey = hotPostPolicy.getPostIdsCacheKey(hotFindingCondition);
        String isLastKey = hotPostPolicy.getLastCacheKey(hotFindingCondition);

        if (isEmpty(hotPost)) {
            cachePostIdsService.delete(postIdsKey);
            cacheIsLastService.delete(isLastKey);
            return;
        }

        final List<Long> hotPostIds = hotPost.stream()
                .map(Post::getId)
                .toList();

        cachePostIdsService.save(postIdsKey, hotPostIds);
        cacheIsLastService.save(isLastKey, hotPost.isLast());
    }

    private boolean isEmpty(Slice<Post> hotPost) {
        return hotPost.get().toList().isEmpty();
    }
}
