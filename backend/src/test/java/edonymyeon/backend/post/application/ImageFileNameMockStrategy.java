package edonymyeon.backend.post.application;

import edonymyeon.backend.image.ImageFileNameStrategy;

public class ImageFileNameMockStrategy implements ImageFileNameStrategy {

    private final String mockPrefix;
    private Long count = -1L;

    public ImageFileNameMockStrategy(final String mockPrefix) {
        this.mockPrefix = mockPrefix;
    }

    @Override
    public String createName(final String originalFileName) {
        String ext = extractExt(originalFileName);
        count++;
        return this.mockPrefix + count + "." + ext;
    }

    private String extractExt(final String originalName) {
        int pos = originalName.lastIndexOf(".");
        return originalName.substring(pos + 1);
    }
}
