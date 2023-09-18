package edonymyeon.backend.support;

import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MockMultipartFileTestSupport {

    public MockMultipartFileBuilder builder() {
        return new MockMultipartFileBuilder();
    }

    public final class MockMultipartFileBuilder {

        private String originalFilename;

        private String contentType;

        private InputStream contentStream;

        public MockMultipartFileBuilder originalFilename(final String originalFilename) {
            this.originalFilename = originalFilename;
            return this;
        }

        public MockMultipartFileBuilder contentType(final String contentType) {
            this.contentType = contentType;
            return this;
        }

        public MockMultipartFileBuilder contentStream(final InputStream contentStream) {
            this.contentStream = contentStream;
            return this;
        }

        public MockMultipartFile buildImageForPost() throws IOException {
            final InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
            return new MockMultipartFile(
                    "newImages",
                    originalFilename == null ? "test_image_2.jpg" : originalFilename,
                    contentType == null ? "image/jpg" : contentType,
                    contentStream == null ? file1InputStream : contentStream
            );
        }
    }
}
