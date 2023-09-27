package edonymyeon.backend.post.application.dto.request;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record PostModificationRequest(
        String title,
        String content,
        Long price,
        List<String> originalImages,
        List<MultipartFile> newImages
) {

}
