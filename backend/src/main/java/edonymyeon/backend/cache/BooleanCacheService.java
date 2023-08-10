package edonymyeon.backend.cache;

import edonymyeon.backend.global.exception.EdonymyeonException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_HAS_NEXT_NOT_FOUND_IN_CACHE;

@RequiredArgsConstructor
@Service
public class BooleanCacheService {

    private final RedisTemplate<String, Boolean> redisTemplate;

    public boolean getHasNext(String hasNextKey) {
        validateHasCache(hasNextKey);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Boolean.class));
        return Boolean.TRUE.equals(redisTemplate.opsForValue().get("key"));
    }

    private void validateHasCache(String hasNextKey) {
        Boolean hasKey = redisTemplate.opsForValue()
                .getOperations()
                .hasKey(hasNextKey);

        if(Objects.isNull(hasKey) || Boolean.FALSE.equals(hasKey)){
            throw new EdonymyeonException(POST_HAS_NEXT_NOT_FOUND_IN_CACHE);
        }
    }

    public void save(String hasNextKey, boolean hasNext) {
        redisTemplate.delete(hasNextKey);
        redisTemplate.opsForValue()
                .append(hasNextKey, String.valueOf(hasNext));
    }
}
