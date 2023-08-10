package edonymyeon.backend.member.domain;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class Device {
    private String deviceToken;
    private LocalDateTime lastAccessedAt;
}
