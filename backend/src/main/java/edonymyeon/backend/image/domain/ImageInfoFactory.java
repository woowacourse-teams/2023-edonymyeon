package edonymyeon.backend.image.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class ImageInfoFactory {

    private final ImageFileNameStrategy imageFileNameStrategy;

    public ImageInfo create(final MultipartFile image) {
        final String originalFileName = image.getOriginalFilename();
        ExtensionValidator.validateExtension(originalFileName);
        final String storeName = imageFileNameStrategy.createName(originalFileName);
        return new ImageInfo(storeName);
    }
}
