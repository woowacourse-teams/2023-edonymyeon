package edonymyeon.backend.service.request;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record PostRequest(
        String title,
        String content,
        Long price,
        List<MultipartFile> images
) {

}

