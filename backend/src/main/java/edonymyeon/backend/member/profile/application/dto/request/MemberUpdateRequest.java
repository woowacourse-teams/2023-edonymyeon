package edonymyeon.backend.member.profile.application.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record MemberUpdateRequest(String nickname, MultipartFile profileImage, boolean isImageChanged) {
}
