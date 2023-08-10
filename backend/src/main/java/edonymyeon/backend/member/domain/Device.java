package edonymyeon.backend.member.domain;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Embeddable
public class Device {
    private String deviceToken;
    private LocalDateTime lastAccessedAt;

    public Device(final String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
