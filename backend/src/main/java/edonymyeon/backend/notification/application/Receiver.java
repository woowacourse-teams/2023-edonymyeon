package edonymyeon.backend.notification.application;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.notification.domain.Data;
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
