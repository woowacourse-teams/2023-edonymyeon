package edonymyeon.backend;

import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.service.ImageFileNameMockStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@TestConfiguration
public class TestConfig {

    @Bean
    public ImageFileUploader imageFileUploader() {
        return new ImageFileUploader(new ImageFileNameMockStrategy(
                "test-inserting-one",
                "test-inserting-two",
                "test-inserting-one",
                "test-inserting-two",
                "test-inserting-one",
                "test-inserting-two"
        ));
    }
}
