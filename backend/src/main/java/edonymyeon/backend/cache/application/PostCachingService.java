package edonymyeon.backend.cache.application;

import edonymyeon.backend.cache.application.dto.CachedPostResponse;
import edonymyeon.backend.cache.domain.CachedHotPost;
import edonymyeon.backend.cache.repository.HotPostsRepository;
import edonymyeon.backend.cache.util.HotPostCachePolicy;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.post.application.HotFindingCondition;
import edonymyeon.backend.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static edonymyeon.backend.global.exception.ExceptionInformation.CACHE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PostCachingService {

    private final HotPostCachePolicy hotPostPolicy;

    public boolean isNotCached(final HotFindingCondition hotFindingCondition) {
        return ! HotPostsRepository.hasKey(hotPostPolicy.getKey(hotFindingCondition));
    }

    public boolean shouldRefreshCache(final HotFindingCondition hotFindingCondition) {
        final CachedHotPost cachedHotPost = HotPostsRepository.get(hotPostPolicy.getKey(hotFindingCondition))
                .orElseThrow(() -> new EdonymyeonException(CACHE_NOT_FOUND));

        return cachedHotPost.shouldRefresh(hotPostPolicy.getExpiredSeconds());

    }

    public CachedPostResponse findCachedPosts(HotFindingCondition hotFindingCondition) {
        final CachedHotPost cachedHotPost = HotPostsRepository.get(hotPostPolicy.getKey(hotFindingCondition))
                .orElseThrow(() -> new EdonymyeonException(CACHE_NOT_FOUND));
        return new CachedPostResponse(cachedHotPost.getPostIds(), cachedHotPost.isLast());
    }

    public void cachePosts(final HotFindingCondition hotFindingCondition, final Slice<Post> hotPost) {
        final List<Long> hotPostIds = hotPost.stream()
                .map(Post::getId)
                .toList();

        if (isNotCached(hotFindingCondition)) {
            saveHotPost(hotFindingCondition, hotPost, hotPostIds);
            return;
        }

        final CachedHotPost cachedHotPost = HotPostsRepository.get(hotPostPolicy.getKey(hotFindingCondition))
                .orElseThrow(() -> new EdonymyeonException(CACHE_NOT_FOUND));
        cachedHotPost.refreshData(hotPostIds, hotPost.isLast());
        HotPostsRepository.save(hotPostPolicy.getKey(hotFindingCondition), cachedHotPost);
    }

    private void saveHotPost(HotFindingCondition hotFindingCondition, Slice<Post> hotPost, List<Long> hotPostIds) {
        final CachedHotPost hotPosts = new CachedHotPost(
                hotPostIds,
                hotPost.isLast(),
                LocalDateTime.now()
        );
        HotPostsRepository.save(hotPostPolicy.getKey(hotFindingCondition), hotPosts);
    }
}
