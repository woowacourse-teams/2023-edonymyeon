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
public class PostCachingService {

    private final BooleanTemplate booleanTemplate;

    private final LongTemplate longTemplate;

    private final HotPostCachePolicy hotPostPolicy;

    public boolean isPostsCached(final HotFindingCondition hotFindingCondition) {
        String postIdsKey = hotPostPolicy.getPostIdsCacheKey(hotFindingCondition);
        String isLastKey = hotPostPolicy.getLastCacheKey(hotFindingCondition);

        return longTemplate.hasCache(postIdsKey) && booleanTemplate.hasCache(isLastKey);
    }

    public CachedPostResponse findCachedPosts(HotFindingCondition hotFindingCondition) {
        String postIdsKey = hotPostPolicy.getPostIdsCacheKey(hotFindingCondition);
        String isLastKey = hotPostPolicy.getLastCacheKey(hotFindingCondition);

        List<Long> postIds = longTemplate.getAllData(postIdsKey);
        boolean isLast = booleanTemplate.getData(isLastKey);
        return new CachedPostResponse(postIds, isLast);
    }

    public void cachePosts(final HotFindingCondition hotFindingCondition, final Slice<Post> hotPost) {
        String postIdsKey = hotPostPolicy.getPostIdsCacheKey(hotFindingCondition);
        String isLastKey = hotPostPolicy.getLastCacheKey(hotFindingCondition);

        if (isEmpty(hotPost)) {
            longTemplate.delete(postIdsKey);
            booleanTemplate.delete(isLastKey);
            return;
        }

        final List<Long> hotPostIds = hotPost.stream()
                .map(Post::getId)
                .toList();

        longTemplate.save(postIdsKey, hotPostIds);
        longTemplate.setExpire(postIdsKey, hotPostPolicy.getExpiredSeconds());
        booleanTemplate.save(isLastKey, hotPost.isLast());
        booleanTemplate.setExpire(isLastKey, hotPostPolicy.getExpiredSeconds());
    }

    private boolean isEmpty(Slice<Post> hotPost) {
        return hotPost.get().toList().isEmpty();
    }
}
