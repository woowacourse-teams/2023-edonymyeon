package edonymyeon.backend.member.application.event;

import edonymyeon.backend.image.domain.ImageInfo;

public record ProfileImageDeletionEvent(ImageInfo imageInfo) {

}
