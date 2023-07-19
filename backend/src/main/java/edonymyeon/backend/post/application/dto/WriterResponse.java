package edonymyeon.backend.post.application.dto;

import edonymyeon.backend.image.postimage.ProfileImageInfo;

public record WriterResponse(long id, String nickname, ProfileImageInfo profileImageInfo) {

}
