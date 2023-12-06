package edonymyeon.backend.content.cache.repository;

import edonymyeon.backend.content.cache.domain.CachedHotPost;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HotPostsRepository {

    private HotPostsRepository() {
    }

    private static final Map<String, CachedHotPost> cacheStorage = new ConcurrentHashMap<>();

    public static boolean hasKey(final String key) {
        return cacheStorage.containsKey(key);
    }

    public static Optional<CachedHotPost> get(final String key) {
        if (hasKey(key)) {
            return Optional.of(cacheStorage.get(key));
        }
        return Optional.empty();
    }

    public static void save(final String key, final CachedHotPost cachedHotPost) {
        cacheStorage.put(key, cachedHotPost);
    }

    public static void delete(final String key) {
        cacheStorage.remove(key);
    }
}
