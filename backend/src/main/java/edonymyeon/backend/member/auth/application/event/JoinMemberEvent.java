package edonymyeon.backend.member.auth.application.event;

import edonymyeon.backend.member.profile.domain.Member;

public record JoinMemberEvent(Member member, String deviceToken) {
}
