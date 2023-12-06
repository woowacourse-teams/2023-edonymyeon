package edonymyeon.backend.member.profile.application.event;

import edonymyeon.backend.image.domain.ImageInfo;
import org.springframework.web.multipart.MultipartFile;

public record ProfileImageUploadEvent(MultipartFile file, ImageInfo imageInfo) {

}
