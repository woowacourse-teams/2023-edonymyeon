package edonymyeon.backend.member.application.dto;

public class AnonymousMemberId extends MemberId {

    public static final long ANONYMOUS_MEMBER_ID = -999L;

    public AnonymousMemberId() {
        super(ANONYMOUS_MEMBER_ID);
    }
}
