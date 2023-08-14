package edonymyeon.backend.cache.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BooleanTemplate {

    private final RedisTemplate<String, Boolean> redisTemplate;

    public boolean hasCache(String key) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .getOperations()
                .hasKey(key));
    }

    public boolean getData(String key) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Boolean.class));
        return Boolean.TRUE.equals(redisTemplate.opsForValue().get(key));
    }

    public void save(String key, boolean value) {
        redisTemplate.delete(key);
        redisTemplate.opsForValue()
                .append(key, String.valueOf(value));
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
