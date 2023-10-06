package edonymyeon.backend;

import edonymyeon.backend.image.ImageFileNameStrategy;
import edonymyeon.backend.post.application.ImageFileNameMockStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@TestConfiguration
public class TestConfig {

    @Bean
    public ImageFileNameStrategy imageFileNameStrategy() {
        return new ImageFileNameMockStrategy("test-inserting");
    }
}
