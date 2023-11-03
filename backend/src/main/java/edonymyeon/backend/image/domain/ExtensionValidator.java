package edonymyeon.backend.image.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_EXTENSION_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;

public class ExtensionValidator {

    public static void validateExtension(final String originalFileName) {
        final String ext = ImageExtension.extractExt(originalFileName);
        if (ImageExtension.contains(ext)) {
            return;
        }
        throw new EdonymyeonException(IMAGE_EXTENSION_INVALID);
    }
}
