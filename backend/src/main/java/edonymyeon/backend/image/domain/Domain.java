package edonymyeon.backend.image.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_DOMAIN_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Domain {

    @Value("${domain}")
    private String domain;

    public List<String> removeDomainFromUrl(final List<String> imageUrls) {
        return imageUrls.stream()
                .map(this::removeDomainFromUrl)
                .toList();
    }

    private String removeDomainFromUrl(final String imageUrl) {
        validateDomainOfUrl(imageUrl);
        return imageUrl.replace(domain, "");
    }

    private void validateDomainOfUrl(final String imageUrl) {
        if (!imageUrl.contains(domain)) {
            throw new EdonymyeonException(IMAGE_DOMAIN_INVALID);
        }
    }
}
