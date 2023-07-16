package edonymyeon.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@TestConfiguration
public class TestConfig {
    @Bean
    public ImageFileUploader imageFileGenerator() {
        return new ImageFileUploader(new ImageFileNameMockStrategy(
                "test-inserting-one",
                "test-inserting-two"
        ));
    }
}
