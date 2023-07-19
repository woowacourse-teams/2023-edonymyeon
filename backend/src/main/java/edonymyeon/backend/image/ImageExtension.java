package edonymyeon.backend.image;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_EXTENSION_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import java.util.Arrays;
import java.util.function.Predicate;
import org.springframework.http.MediaType;

public enum ImageExtension {
    JPG, JPEG, PNG;

    public static boolean contains(String extension) {
        return Arrays.stream(values())
                .map(Enum::toString)
                .anyMatch(Predicate.isEqual(extension.toUpperCase()));
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
        return Arrays.stream(values())
                .filter(ext -> ext.toString().equals(extension.toUpperCase()))
                .findAny()
                .orElseThrow(() -> new EdonymyeonException(IMAGE_EXTENSION_INVALID));
    }
}
