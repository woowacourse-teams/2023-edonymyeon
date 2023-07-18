package edonymyeon.backend.image;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ImageFileNameUUIDStrategy implements ImageFileNameStrategy {

    @Override
    public String createName(final String originalFileName) {
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(final String originalName) {
        int pos = originalName.lastIndexOf(".");
        return originalName.substring(pos + 1);
    }
}
