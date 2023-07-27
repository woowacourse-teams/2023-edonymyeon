package edonymyeon.backend.image.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_DOMAIN_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Domain {

    @Value("${domain}")
    private String domain;

    public String removeDomainFromUrl(final String imageUrl) {
        if (!imageUrl.contains(domain)) {
            throw new EdonymyeonException(IMAGE_DOMAIN_INVALID);
        }
        return imageUrl.replace(domain, "");
    }
}
