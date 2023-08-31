package edonymyeon.backend.member.application.dto;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = {"id"})
public abstract class MemberId {

    private final Long id;

    protected MemberId(final Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }
}
