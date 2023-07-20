package edonymyeon.backend.image.domain;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Domain {

    @Value("${domain}")
    private String domain;
}
