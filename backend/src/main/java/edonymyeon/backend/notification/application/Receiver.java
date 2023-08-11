package edonymyeon.backend.notification.application;

import edonymyeon.backend.member.domain.Member;
import lombok.Getter;

@Getter
public class Receiver {

    private final String token;

    public Receiver(final Member member) {
        this.token = member.getDeviceToken();
    }
}
