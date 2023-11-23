package edonymyeon.backend;

import edonymyeon.backend.content.cache.util.HotPostCachePolicy;
import edonymyeon.backend.content.post.application.TestCacheKeyStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CacheConfig {

    @Bean
    public HotPostCachePolicy hotPostCachePolicy(){
        return new HotPostCachePolicy(new TestCacheKeyStrategy());
    }
}
