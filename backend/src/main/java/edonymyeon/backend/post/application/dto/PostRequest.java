package edonymyeon.backend.post.application.dto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record PostRequest(
        String title,
        String content,
        Long price,
        List<MultipartFile> newImages
) {

}

