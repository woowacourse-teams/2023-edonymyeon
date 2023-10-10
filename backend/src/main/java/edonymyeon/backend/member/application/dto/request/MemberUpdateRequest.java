package edonymyeon.backend.member.application.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record MemberUpdateRequest(String nickname, MultipartFile profileImage, boolean isImageChanged) {
}
