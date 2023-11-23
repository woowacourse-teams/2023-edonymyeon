package edonymyeon.backend.membber.member.application.dto;

public final class AnonymousMemberId extends MemberId {

    public static final long ANONYMOUS_MEMBER_ID = -999L;

    public AnonymousMemberId() {
        super(ANONYMOUS_MEMBER_ID);
    }
}
