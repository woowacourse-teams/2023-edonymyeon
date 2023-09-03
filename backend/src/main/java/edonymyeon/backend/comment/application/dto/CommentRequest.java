package edonymyeon.backend.comment.application.dto;

import org.springframework.web.multipart.MultipartFile;

public record CommentRequest(MultipartFile image, String comment) {

}
