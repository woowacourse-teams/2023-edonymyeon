package edonymyeon.backend;

import edonymyeon.backend.post.application.TestCacheKeyStrategy;
import edonymyeon.backend.post.domain.HotPostPolicy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CacheConfig {

    @Bean
    public HotPostPolicy hotPostPolicy(){
        return new HotPostPolicy(new TestCacheKeyStrategy());
    }
}
