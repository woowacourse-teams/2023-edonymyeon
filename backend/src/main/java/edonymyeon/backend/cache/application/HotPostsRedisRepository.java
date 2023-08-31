package edonymyeon.backend.cache.application;

import edonymyeon.backend.cache.domain.CachedHotPost;
import org.springframework.data.repository.CrudRepository;

public interface HotPostsRedisRepository extends CrudRepository<CachedHotPost, String> {
}
