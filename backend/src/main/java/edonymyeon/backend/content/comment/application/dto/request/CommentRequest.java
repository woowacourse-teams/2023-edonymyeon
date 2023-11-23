package edonymyeon.backend.content.comment.application.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record CommentRequest(MultipartFile image, String comment) {

}
