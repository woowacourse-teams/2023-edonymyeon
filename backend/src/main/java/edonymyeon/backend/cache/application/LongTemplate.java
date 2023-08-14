package edonymyeon.backend.cache.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class LongTemplate {

    private final RedisTemplate<String, Long> redisTemplate;

    public boolean hasCache(String key) {
        return Boolean.TRUE.equals(redisTemplate.opsForList().getOperations().hasKey(key));
    }

    public List<Long> getAllData(String key) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Long.class));
        Long len = redisTemplate.opsForList().size(key);
        return len == 0 ? new ArrayList<>() : redisTemplate.opsForList().range(key, 0, len - 1);
    }

    public void save(String postIdsKey, List<Long> hotPostIds) {
        redisTemplate.delete(postIdsKey);
        redisTemplate.opsForList().leftPushAll(postIdsKey, hotPostIds);
    }

    public void delete(String postIdsKey) {
        redisTemplate.delete(postIdsKey);
    }
}
