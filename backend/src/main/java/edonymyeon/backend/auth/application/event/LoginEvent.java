package edonymyeon.backend.auth.application.event;

import edonymyeon.backend.member.domain.Member;

public record LoginEvent(Member member, String deviceToken) {
}
