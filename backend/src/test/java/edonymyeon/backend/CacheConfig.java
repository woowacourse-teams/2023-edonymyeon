package edonymyeon.backend;

import edonymyeon.backend.cache.util.HotPostCachePolicy;
import edonymyeon.backend.post.application.TestCacheKeyStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CacheConfig {

    @Bean
    public HotPostCachePolicy hotPostCachePolicy(){
        return new HotPostCachePolicy(new TestCacheKeyStrategy());
    }

    // test container 설정
//    private static final String REDIS_DOCKER_IMAGE = "redis:5.0.3-alpine";
//
//    static {
//        GenericContainer<?> REDIS_CONTAINER =
//                new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
//                        .withExposedPorts(6379)
//                        .withReuse(true);
//
//        REDIS_CONTAINER.start();
//
//        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
//        System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
//    }
}
