package edonymyeon.backend.cache.application;

import edonymyeon.backend.cache.application.domain.CachedHotPost;
import edonymyeon.backend.cache.application.dto.CachedPostResponse;
import edonymyeon.backend.cache.util.HotPostCachePolicy;
import edonymyeon.backend.post.application.HotFindingCondition;
import edonymyeon.backend.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCachingService {

    private final HotPostsRedisRepository hotPostsRedisRepository;

    private final HotPostCachePolicy hotPostPolicy;

    public boolean isNotCached(final HotFindingCondition hotFindingCondition) {
        Optional<CachedHotPost> cachedPost = hotPostsRedisRepository.findById(hotPostPolicy.getKey(hotFindingCondition));
        return cachedPost.isEmpty();
    }

    public boolean shouldRefreshCache(final HotFindingCondition hotFindingCondition) {
        CachedHotPost cachedHotPost = hotPostsRedisRepository.findById(hotPostPolicy.getKey(hotFindingCondition))
                .orElseThrow();
        return cachedHotPost.shouldRefresh(hotPostPolicy.getExpiredSeconds());
    }

    public CachedPostResponse findCachedPosts(HotFindingCondition hotFindingCondition) {
        CachedHotPost cachedHotPost = hotPostsRedisRepository.findById(hotPostPolicy.getKey(hotFindingCondition))
                .orElseThrow();
        return new CachedPostResponse(cachedHotPost.getPostIds(), cachedHotPost.isLast());
    }

    public void cachePosts(final HotFindingCondition hotFindingCondition, final Slice<Post> hotPost) {
        Optional<CachedHotPost> hotPostsFromCache = hotPostsRedisRepository.findById(hotPostPolicy.getKey(hotFindingCondition));

        final List<Long> hotPostIds = hotPost.stream()
                .map(Post::getId)
                .toList();

        if (hotPostsFromCache.isEmpty()) {
            saveHotPost(hotFindingCondition, hotPost, hotPostIds);
            return;
        }

        CachedHotPost cachedHotPost = hotPostsFromCache.get();
        if(cachedHotPost.shouldRefresh(hotPostPolicy.getExpiredSeconds())){
            cachedHotPost.refreshData(hotPostIds, hotPost.isLast());
            hotPostsRedisRepository.save(cachedHotPost);
        }
    }

    private void saveHotPost(HotFindingCondition hotFindingCondition, Slice<Post> hotPost, List<Long> hotPostIds) {
        CachedHotPost hotPosts = new CachedHotPost(
                hotPostPolicy.getKey(hotFindingCondition),
                hotPostIds,
                hotPost.isLast(),
                LocalDateTime.now()
        );
        hotPostsRedisRepository.save(hotPosts);
    }
}
