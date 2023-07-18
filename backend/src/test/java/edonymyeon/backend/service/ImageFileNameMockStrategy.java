package edonymyeon.backend.service;

import static org.junit.Assert.fail;

import edonymyeon.backend.image.ImageFileNameStrategy;
import java.util.Arrays;
import java.util.Iterator;

public class ImageFileNameMockStrategy implements ImageFileNameStrategy {

    private final Iterator<String> mockPrefixes;

    public ImageFileNameMockStrategy(final String... mockPrefixes) {
        this.mockPrefixes = Arrays.stream(mockPrefixes).iterator();
    }

    @Override
    public String createName(final String originalFileName) {
        if (!mockPrefixes.hasNext()) {
            fail("모킹할 prefix가 남아있지 않습니다");
        }
        final String prefix = mockPrefixes.next();
        String ext = extractExt(originalFileName);
        return prefix + "." + ext;
    }

    private String extractExt(final String originalName) {
        int pos = originalName.lastIndexOf(".");
        return originalName.substring(pos + 1);
    }
}
