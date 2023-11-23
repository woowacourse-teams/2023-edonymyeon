package edonymyeon.backend.membber.auth.application.event;

import edonymyeon.backend.membber.member.domain.Member;

public record JoinMemberEvent(Member member, String deviceToken) {
}
