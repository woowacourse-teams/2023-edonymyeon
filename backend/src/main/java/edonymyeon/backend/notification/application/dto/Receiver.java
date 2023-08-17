package edonymyeon.backend.notification.application.dto;

import edonymyeon.backend.member.domain.Member;
import lombok.Getter;

@Getter
public class Receiver {

    private final String token;
    private final Data data;

    public Receiver(final Member member, final Data data) {
        this.token = member.getActiveDeviceToken().get();
        this.data = data;
    }
}
