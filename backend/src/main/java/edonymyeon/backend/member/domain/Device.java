package edonymyeon.backend.member.domain;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class Device {
    private String deviceToken;
    private LocalDateTime lastAccessedAt;

    public Device(final String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
