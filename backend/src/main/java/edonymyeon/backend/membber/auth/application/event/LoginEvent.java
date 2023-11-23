package edonymyeon.backend.membber.auth.application.event;

import edonymyeon.backend.membber.member.domain.Member;

public record LoginEvent(Member member, String deviceToken) {
}
