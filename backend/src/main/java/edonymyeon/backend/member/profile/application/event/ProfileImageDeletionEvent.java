package edonymyeon.backend.member.profile.application.event;

import edonymyeon.backend.image.domain.ImageType;
import edonymyeon.backend.image.domain.ImageInfo;

public record ProfileImageDeletionEvent(ImageInfo imageInfo, ImageType imageType) {

}
