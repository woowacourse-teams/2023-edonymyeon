package edonymyeon.backend.member.auth.application.event;

import edonymyeon.backend.member.profile.domain.Member;

public record LoginEvent(Member member, String deviceToken) {
}
