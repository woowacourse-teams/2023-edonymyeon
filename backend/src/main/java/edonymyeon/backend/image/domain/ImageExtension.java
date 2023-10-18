package edonymyeon.backend.image.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_EXTENSION_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.MediaType;

public enum ImageExtension {
    JPG, JPEG, PNG;

    private static final Map<String, ImageExtension> extensions = Map.of(
            JPG.name(), JPG,
            JPEG.name(), JPEG,
            PNG.name(), PNG
    );

    public static boolean contains(String extension) {
        return extensions.containsKey(extension.toUpperCase());
    }

    public static MediaType findMediaType(String fileName) {
        final String ext = extractExt(fileName);
        final ImageExtension extension = from(ext);
        return switch (extension) {
            case JPG, JPEG -> MediaType.IMAGE_JPEG;
            case PNG -> MediaType.IMAGE_PNG;
        };
    }

    public static String extractExt(String fileName) {
        if (!fileName.contains(".")) {
            throw new EdonymyeonException(IMAGE_EXTENSION_INVALID);
        }
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos + 1);
    }

    public static ImageExtension from(String extension) {
        final ImageExtension imageExtension = extensions.get(extension.toUpperCase());
        if (Objects.isNull(imageExtension)) {
            throw new EdonymyeonException(IMAGE_EXTENSION_INVALID);
        }
        return imageExtension;
    }
}
