package edonymyeon.backend.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LongCacheService {

    private final RedisTemplate<String, Long> redisTemplate;

    public boolean hasCache(String key) {
        return Boolean.TRUE.equals(redisTemplate.opsForList().getOperations().hasKey(key));
    }

    public List<Long> getPostIds(String key) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Long.class));
        Long len = redisTemplate.opsForList().size(key);
        return len == 0 ? new ArrayList<>() : redisTemplate.opsForList().range(key, 0, len - 1);
    }

    public void save(String postIdsKey, List<Long> hotPostIds) {
        redisTemplate.delete(postIdsKey);
        redisTemplate.opsForList().leftPushAll(postIdsKey, hotPostIds);
    }
}
