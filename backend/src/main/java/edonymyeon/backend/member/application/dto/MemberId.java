package edonymyeon.backend.member.application.dto;

public class MemberId {
    private final Long id;

    public MemberId(final Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }
}
