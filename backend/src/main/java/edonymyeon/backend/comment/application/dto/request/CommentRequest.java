package edonymyeon.backend.comment.application.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record CommentRequest(MultipartFile image, String comment) {

}
