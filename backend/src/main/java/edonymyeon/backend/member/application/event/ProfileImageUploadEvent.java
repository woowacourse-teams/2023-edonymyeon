package edonymyeon.backend.member.application.event;

import edonymyeon.backend.image.domain.ImageInfo;
import org.springframework.web.multipart.MultipartFile;

public record ProfileImageUploadEvent(MultipartFile file, ImageInfo imageInfo) {

}
