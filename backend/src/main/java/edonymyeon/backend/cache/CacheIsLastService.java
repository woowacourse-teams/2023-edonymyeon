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
public class CacheIsLastService {

    private final RedisTemplate<String, Boolean> redisTemplate;

    public boolean getHasNext(String isLastKey) {
        validateHasCache(isLastKey);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Boolean.class));
        return Boolean.TRUE.equals(redisTemplate.opsForValue().get("key"));
    }

    private void validateHasCache(String isLastKey) {
        Boolean hasKey = redisTemplate.opsForValue()
                .getOperations()
                .hasKey(isLastKey);

        if(Objects.isNull(hasKey) || Boolean.FALSE.equals(hasKey)){
            throw new EdonymyeonException(POST_HAS_NEXT_NOT_FOUND_IN_CACHE);
        }
    }

    public void save(String isLastKey, boolean hasNext) {
        redisTemplate.delete(isLastKey);
        redisTemplate.opsForValue()
                .append(isLastKey, String.valueOf(hasNext));
    }

    public void delete(String isLastKey) {
        redisTemplate.delete(isLastKey);
    }
}
