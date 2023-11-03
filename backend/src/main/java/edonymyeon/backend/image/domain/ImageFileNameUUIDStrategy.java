package edonymyeon.backend.image.domain;

import edonymyeon.backend.image.domain.ImageExtension;
import edonymyeon.backend.image.domain.ImageFileNameStrategy;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ImageFileNameUUIDStrategy implements ImageFileNameStrategy {

    @Override
    public String createName(final String originalFileName) {
        String ext = ImageExtension.extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }
}
