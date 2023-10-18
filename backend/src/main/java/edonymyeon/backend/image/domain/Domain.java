package edonymyeon.backend.image.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_DOMAIN_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Domain {

    @Value("${image.domain}")
    private String domain;

    public List<String> removeDomainFromUrl(final List<String> imageUrls, final String typeDirectory) {
        return imageUrls.stream()
                .map(each -> removeDomainFromUrl(each, typeDirectory))
                .toList();
    }

    private String removeDomainFromUrl(final String imageUrl, final String typeDirectory) {
        validateDomainOfUrl(imageUrl);
        return imageUrl.replace(domain, "")
                .replace(typeDirectory, "");
    }

    private void validateDomainOfUrl(final String imageUrl) {
        if (!imageUrl.contains(domain)) {
            throw new EdonymyeonException(IMAGE_DOMAIN_INVALID);
        }
    }

    @Nullable
    public String convertToImageUrl(final ImageInfo imageInfo) {
        if(imageInfo == null){
            return null;
        }
        final String imageFileName = imageInfo.getStoreName();
        return domain + imageFileName;
    }
}
