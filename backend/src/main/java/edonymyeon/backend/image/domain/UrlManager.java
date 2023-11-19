package edonymyeon.backend.image.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_DOMAIN_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class UrlManager {

    @Value("${image.domain}")
    private String domain;

    public List<String> convertToStoreName(final List<String> imageUrls, final String typeDirectory) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return Collections.emptyList();
        }
        return imageUrls.stream()
                .map(each -> convertToStoreName(each, typeDirectory))
                .toList();
    }

    private String convertToStoreName(final String imageUrl, final String typeDirectory) {
        validateDomainOfUrl(imageUrl);
        return imageUrl.replace(domain, "")
                .replace(typeDirectory, "");
    }

    private void validateDomainOfUrl(final String imageUrl) {
        if (!imageUrl.contains(domain)) {
            log.info("imageUrl: {}", imageUrl);
            throw new EdonymyeonException(IMAGE_DOMAIN_INVALID);
        }
    }

    @Nullable
    public String convertToImageUrl(final ImageType imageType, final ImageInfo imageInfo) {
        if(imageInfo == null){
            return null;
        }
        final String imageFileName = imageInfo.getStoreName();
        return domain + imageType.getSaveDirectory() + imageFileName;
    }

    public String findBaseUrl(final ImageType imageType) {
        return domain + imageType.getSaveDirectory();
    }
}
