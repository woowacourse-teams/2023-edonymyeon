package edonymyeon.backend.membber.member.application.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record MemberUpdateRequest(String nickname, MultipartFile profileImage, boolean isImageChanged) {
}
